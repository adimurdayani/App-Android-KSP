package com.eka.koperasisimpanpinjam.network.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.activity.DetailPinjamanActivity;
import com.eka.koperasisimpanpinjam.network.model.DataPinjaman;
import com.eka.koperasisimpanpinjam.network.model.DataSimpanan;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class PinjamanAdapter extends RecyclerView.Adapter<PinjamanAdapter.HolderData> {

    private Context context;
    private ArrayList<DataPinjaman> dataPinjamen;

    public PinjamanAdapter(Context context, ArrayList<DataPinjaman> dataPinjamen) {
        this.context = context;
        this.dataPinjamen = dataPinjamen;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_pinjaman, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataPinjaman pinjaman = dataPinjamen.get(position);

        holder.txt_id.setText(String.valueOf(pinjaman.getId()));
        holder.txt_keterangan.setText(pinjaman.getKeterangan());
        holder.txt_tanggal.setText(pinjaman.getCreated_at());
        holder.txt_nomember.setText(pinjaman.getMember_id());
        holder.txt_nopinjaman.setText(pinjaman.getNo_pinjaman());

        String format = formatRupiah(Double.parseDouble(String.valueOf(pinjaman.getJumlah())));
        int tenor = pinjaman.getTenor();
        int color_text = context.getColor(R.color.belum_lunas_teks);
        int color_div = context.getColor(R.color.belum_lunas);
        if (tenor == 0) {
            color_div = context.getColor(R.color.lunas);
            color_text = context.getColor(R.color.lunas_teks);
            holder.txt_jumlahpinjaman.setText(formatRupiah(Double.parseDouble("0")));
        } else {
            holder.txt_jumlahpinjaman.setText(format);
        }
        holder.txt_jumlahpinjaman.setTextColor(color_text);
        holder.teks_pinjaman.setTextColor(color_text);
        holder.div_status.setBackgroundColor(color_div);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPinjamanActivity.class);
                intent.putExtra("id", String.valueOf(pinjaman.getId()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPinjamen.size();
    }

    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DataPinjaman> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(dataPinjamen);
            } else {
                for (DataPinjaman getpinjaman : dataPinjamen) {
                    if (getpinjaman.getNo_pinjaman().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getpinjaman.getMember_id().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getpinjaman.getKeterangan().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getpinjaman.getCreated_at().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        searchList.add(getpinjaman);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = searchList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataPinjamen.clear();
            dataPinjamen.addAll((Collection<? extends DataPinjaman>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderData extends RecyclerView.ViewHolder {

        private TextView txt_nopinjaman, txt_nomember, txt_keterangan, txt_tanggal, txt_id, txt_jumlahpinjaman, teks_pinjaman;
        RelativeLayout detail, div_status;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            txt_nopinjaman = itemView.findViewById(R.id.no_pinjaman);
            txt_nomember = itemView.findViewById(R.id.no_member);
            txt_keterangan = itemView.findViewById(R.id.keterangan);
            txt_tanggal = itemView.findViewById(R.id.teks_tanggal);
            txt_id = itemView.findViewById(R.id.id);
            txt_jumlahpinjaman = itemView.findViewById(R.id.jumlah_pinjaman);
            detail = itemView.findViewById(R.id.detail);
            teks_pinjaman = itemView.findViewById(R.id.teks_pinjaman);
            div_status = itemView.findViewById(R.id.div_status);
        }
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
