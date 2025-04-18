package uz.rezanen.task.remote.providers

import uz.rezanen.task.shp.Shp
import uz.rezanen.task.utils.Const

class PhoneNumberProvider(
    private val shp: Shp
) {
    @Volatile
    private var phone: String? = shp.getString(Const.phone)

    fun setPhone(value: String) {
        shp.setString(Const.phone, value)
        phone = value
    }

    fun clearPhone() {
        shp.removeString(Const.phone)
        phone = null
    }

    fun getPhone(): String? = phone
}