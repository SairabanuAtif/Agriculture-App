package com.triocodes.krishikkaran.Interface;

import java.util.HashMap;

/**
 * Created by admin on 25-02-16.
 */
public interface VolleyCallback  {
    void volleyOnSuccess();
    void volleyOnError();
    HashMap<String,Object>getExtras();
}
