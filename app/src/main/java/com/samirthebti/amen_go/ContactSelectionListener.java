package com.samirthebti.amen_go;

import mx.com.quiin.contactpicker.Contact;

/**
 * Amen_Go
 * Created by Samir Thebti on 5/16/17.
 * thebtisam@gmail.com
 */

public interface ContactSelectionListener {
    void onContactSelected(Contact contact, String communication);
    void onContactDeselected(Contact contact, String communication);
    void onContactDelate(Contact contact, String communication);
}
