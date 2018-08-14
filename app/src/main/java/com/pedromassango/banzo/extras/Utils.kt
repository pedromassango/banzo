package com.pedromassango.banzo.extras

import com.pedromassango.banzo.MainApplication

/**
 * Created by Pedro Massango on 7/15/18.
 */


val runOnFree = {code: ()-> Unit ->

    if(!MainApplication.isPro()){
        code()
    }
}