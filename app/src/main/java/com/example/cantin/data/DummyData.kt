package com.example.cantin.data

import com.example.cantin.R
import com.example.cantin.model.MenuCategory
import com.example.cantin.model.MenuItem 

val DUMMY_MENU_ITEMS = listOf(
    MenuItem(1, "Nasi Ayam Geprek", 15000, R.drawable.nasi_ayam_geprek, null, MenuCategory.MAKANAN, "Nasi + Ayam Geprek + Lalapan + Sambal Bawang"),
    MenuItem(2, "Nasi Goreng", 10000, R.drawable.nasi_goreng, null, MenuCategory.MAKANAN, "Nasi Goreng Spesial + Telur Mata Sapi + Kerupuk + Acar"),
    MenuItem(3, "Nasi Soto Ayam", 10000, R.drawable.nasi_soto_ayam, null, MenuCategory.MAKANAN, "Nasi + Soto Ayam Kuah Kuning + Suwir Ayam + Tauge"),
    MenuItem(4, "Chicken Katsu", 15000, R.drawable.chicken_katsu, null, MenuCategory.MAKANAN, "Nasi + Chicken Katsu + Salad + Saus"),
    MenuItem(5, "Mie Goreng", 10000, R.drawable.mie_goreng, null, MenuCategory.MAKANAN, "Mie Goreng Jawa + Sayuran + Telur + Kerupuk"),

    MenuItem(6, "teh es", 5000, R.drawable.teh_es, null, MenuCategory.MINUMAN, "Teh Manis Dingin Segar"),
    MenuItem(7, "Jus Jeruk", 7000, R.drawable.jus_jeruk, null, MenuCategory.MINUMAN, "Jus Jeruk Murni Segar"),
    MenuItem(8, "Jus Alpukat", 10000, R.drawable.jus_pokat, null, MenuCategory.MINUMAN, "Jus Alpukat + Susu Coklat"),
    MenuItem(9, "Cappuccino", 6000, R.drawable.capcin, null, MenuCategory.MINUMAN, "Es Cappuccino Cincau"),
    MenuItem(10, "Lemon Tea", 5000, R.drawable.lemon_tea, null, MenuCategory.MINUMAN, "Teh Lemon Dingin Segar")
)


