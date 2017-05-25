package com.samirthebti.amen_go;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.facebook.stetho.common.StringUtil;
import com.samirthebti.amen_go.Adapter.MyContactRecyclerViewAdapter;
import com.samirthebti.amen_go.Model.MyContact;
import com.samirthebti.amen_go.services.GpsReceiver;
import com.samirthebti.amen_go.services.MyLocationService;
import com.samirthebti.amen_go.utils.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import mx.com.quiin.contactpicker.Contact;
import mx.com.quiin.contactpicker.SimpleContact;
import mx.com.quiin.contactpicker.ui.ContactPickerActivity;

public class StartActivity extends BaseActivity implements ContactSelectionListener {
    public static final String TAG = StartActivity.class.getSimpleName();
    //    List of speeds
    public static final List<Integer> speeds = new ArrayList<>(Arrays.asList(0,1, 20, 40, 60, 80, 100, 120));
    private static final int READ_CONTACT_REQUEST = 1;
    private static final int CONTACT_PICKER_REQUEST = 2;
    private static final int REQUEST_CODE_SHARE = 0;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private RecyclerView mRecyclerView;
    private Context context;
    private Realm realm;

    private List<MyContact> contactList = new ArrayList<>();
    private Spinner speedSpinner;
    private MyContactRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        context = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startService();
        } else {
            Intent myIntent = new Intent(context, MyLocationService.class);
            startService(myIntent);
        }
        realm = Realm.getDefaultInstance();
        contactList = realm.where(MyContact.class).findAll();
        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, GpsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 120000,
                pendingIntent);

        //Config Spinner
        speedSpinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_item, speeds);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(dataAdapter);
        int savedPosition = Preferences.readSharedPreference(context, "position", 0);
        speedSpinner.setSelection(savedPosition);

        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Preferences.writeSharedPreference(context, "maxSpeed", parent.getItemAtPosition(position).toString());
                Preferences.writeSharedPreference(context, "position", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        if (contactList != null)
            setRecyclerView();
        //Share the app
        ImageView shareImageView = (ImageView) findViewById(R.id.share);
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String deeplink = "Amen Go ";
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, deeplink);
                    startActivityForResult(Intent.createChooser(intent, "Share The App"), REQUEST_CODE_SHARE);

                } catch (Exception e) {
                    Log.e(TAG, "", e);
                    finish();
                }
            }
        });
        // add new authorized contacts
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchContactPicker(null);
            }
        });
    }

    public void launchContactPicker(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent contactPicker = new Intent(this, ContactPickerActivity.class);
            startActivityForResult(contactPicker, CONTACT_PICKER_REQUEST);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACT_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONTACT_PICKER_REQUEST:
                //contacts were selected
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        TreeSet<SimpleContact> selectedContacts = (TreeSet<SimpleContact>) data.getSerializableExtra(ContactPickerActivity.CP_SELECTED_CONTACTS);
                        if (selectedContacts != null) {
                            contactList = realm.where(MyContact.class).findAll();
                            for (SimpleContact selectedContact : selectedContacts) {
                                final MyContact contact = new MyContact(selectedContact.getDisplayName(), selectedContact.getCommunication());
                                Log.d(TAG, "onActivityResult: " + selectedContact.toString());
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        MyContact myContact = realm.where(MyContact.class).equalTo("phone", contact.getPhone()).findFirst();
                                        if (myContact == null) {
                                            String bb = StringUtil.removeAll(contact.getPhone(), '+');
                                            String nbr = StringUtil.removeAll(bb, '-');
                                            MyContact ctct = realm.createObject(MyContact.class, StringUtil.removeAll(nbr, ' '));
                                            ctct.setName(contact.getName());

                                        }
                                    }
                                });
                            }
                        }
                    }
                    contactList = realm.where(MyContact.class).findAll();
                    setRecyclerView();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setRecyclerView() {
        adapter = new MyContactRecyclerViewAdapter(contactList, new MyContactRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onClick(final MyContact contact) {
                showProgressDialog();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MyContact myContact = realm.where(MyContact.class).equalTo("phone", contact.getPhone()).findFirst();
                        if (myContact != null) {
                            myContact.deleteFromRealm();
                        }
                    }
                });
                hideProgressDialog();
            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onContactSelected(Contact contact, String communication) {

    }

    @Override
    public void onContactDeselected(Contact contact, String communication) {

    }

    @Override
    public void onContactDelate(Contact contact, String communication) {
        Log.d(TAG, "onContactDelate: " + contact.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startService() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Coarse location");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts");
        if (!addPermission(permissionsList, Manifest.permission.PROCESS_OUTGOING_CALLS))
            permissionsNeeded.add("outgoing calls");
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Call phone");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("Call phone");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("phone State");
        if (!addPermission(permissionsList, Manifest.permission.MODIFY_PHONE_STATE))
            permissionsNeeded.add("phone State");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);

                    Log.d(TAG, "onRequestPermissionsResult: " + i);
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.PROCESS_OUTGOING_CALLS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MODIFY_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.MODIFY_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Intent myIntent = new Intent(context, MyLocationService.class);
                    startService(myIntent);
                } else {
                    //startService();
                    Intent myIntent = new Intent(context, MyLocationService.class);
                    startService(myIntent);
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}

