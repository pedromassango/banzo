package com.pedromassango.banzo.extras

import com.pedromassango.banzo.BanzoApp

/**
 * Created by Pedro Massango on 7/15/18.
 */


val runOnFree = {code: ()-> Unit ->

    if(!BanzoApp.isPro()){
        code()
    }
}