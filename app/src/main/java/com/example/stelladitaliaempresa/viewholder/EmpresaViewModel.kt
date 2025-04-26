package com.example.stelladitaliaempresa.viewholder

import android.os.Parcel
import android.os.Parcelable

data class EmpresaViewModel(
    var empresa: String? = null,
    var categoria: String? = null,
    var email: String? = null,
    var taxaEntrega: Double? = null,
    var imagem: String? = null,
    var idUsuario: String? = null


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(empresa)
        parcel.writeString(categoria)
        parcel.writeString(email)
        parcel.writeValue(taxaEntrega)
        parcel.writeString(imagem)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<EmpresaViewModel> {
        override fun createFromParcel(parcel: Parcel): EmpresaViewModel {
            return EmpresaViewModel(parcel)
        }

        override fun newArray(size: Int): Array<EmpresaViewModel?> {
            return arrayOfNulls(size)
        }
    }
    fun salvar() {
        val firebaseRef = com.google.firebase.database.FirebaseDatabase.getInstance()
        val empresaRef = firebaseRef.getReference("empresa")
        idUsuario?.let {
            empresaRef.child(it).setValue(this)
        }
    }


}
