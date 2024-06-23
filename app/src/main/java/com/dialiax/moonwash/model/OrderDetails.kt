package com.dialiax.moonwash.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

class OrderDetails():Serializable {
    var userUid: String? = null
    var userName: String? = null
    var Names: List<String>? = null
    var Images: List<String>? = null
    var Prices: List<String>? = null
    var Quantities: List<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var orderExecute : Boolean = false
    var orderDelivery: Boolean = false
    var orderCompleted : Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        orderExecute = parcel.readByte() != 0.toByte()
        orderDelivery = parcel.readByte() != 0.toByte()
        orderCompleted = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userId: String,
        name: String,
        ItemsName: ArrayList<String>,
        ItemsImage: ArrayList<String>,
        ItemsPrice: ArrayList<String>,
        ItemsQuantity: ArrayList<Int>,
        address: String,
        totalAmount: String,
        phone: String,
        time: Long,
        itemPushKey: String?,
        b: Boolean,
        b1: Boolean,
        b2: Boolean,
        b3: Boolean,
        b4: Boolean
    ) : this(){
        this.userUid = userId
        this.userName = name
        this.Names = ItemsName
        this.Images = ItemsImage
        this.Prices = ItemsPrice
        this.Quantities = ItemsQuantity
        this.address = address
        this.totalPrice = totalAmount
        this.phoneNumber = phone
        this.currentTime = time
        this.itemPushKey = itemPushKey
        this.orderAccepted = b
        this.paymentReceived = b1
        this.orderExecute = b2
        this.orderDelivery = b3
        this.orderCompleted = b4
    }

 fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeByte(if (orderExecute) 1 else 0)
        parcel.writeByte(if (orderDelivery) 1 else 0)
        parcel.writeByte(if (orderCompleted)1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

 fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }


}