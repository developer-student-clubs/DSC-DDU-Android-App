package com.dscddu.dscddu.Listeners;

import android.os.Bundle;

public interface FragmentActionListener  {
    int ACTION_VALUE_EVENT_DETAILS = 10;
    String ACTION_KEY = "key";
    String REGISTRATION_MSG = "msg";
    int ACTION_VALUE_REGISTER = 0;  ///For successful registeration
    int ACTION_VALUE_BACK_TO_HOME = 11;
    int ACTION_NO_INTERNET = 404;


    void actionPerformed(Bundle bundle);
}
