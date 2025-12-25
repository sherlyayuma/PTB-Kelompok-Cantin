package com.example.cantin.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(amount).replace("Rp", "Rp. ").replace(",00", "")
}
