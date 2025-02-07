package com.example.mobiswitch

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WiFiAdapter(private val wifiList: List<ScanResult>, private val onItemClick: (ScanResult) -> Unit) :
    RecyclerView.Adapter<WiFiAdapter.WiFiViewHolder>() {

    class WiFiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wifiName: TextView = itemView.findViewById(R.id.txtWifiName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WiFiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wifi, parent, false)
        return WiFiViewHolder(view)
    }

    override fun onBindViewHolder(holder: WiFiViewHolder, position: Int) {
        val wifi = wifiList[position]
        holder.wifiName.text = wifi.SSID
        holder.itemView.setOnClickListener { onItemClick(wifi) }
    }

    override fun getItemCount() = wifiList.size
}
