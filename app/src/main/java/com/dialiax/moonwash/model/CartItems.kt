package com.dialiax.moonwash.model

data class CartItems(
    var Name: String? = null,
    var Price: String? = null,
    var Description: String? = null,
    var Image: String? = null,
    var Quantity: Int = 0,
    var Estimasi: String? = null
) {
    constructor() : this(null, null, null, null, 0, null)
}
