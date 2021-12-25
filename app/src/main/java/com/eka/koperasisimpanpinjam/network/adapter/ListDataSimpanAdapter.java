package com.eka.koperasisimpanpinjam.network.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.network.model.DataSimpanan;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class ListDataSimpanAdapter extends RecyclerView.Adapter<ListDataSimpanAdapter.HolderData> {
    private Context context;
    private ArrayList<DataSimpanan> dataSimpanans;

    public ListDataSimpanAdapter(Context context, ArrayList<DataSimpanan> dataSimpanans) {
        this.context = context;
        this.dataSimpanans = dataSimpanans;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_simpanan, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataSimpanan simpanan = dataSimpanans.get(position);

        holder.txt_id.setText(String.valueOf(simpanan.getId()));
        holder.txt_member.setText(simpanan.getMember_id());
        holder.txt_catatan.setText(simpanan.getCatatan());
        holder.txt_tanggal.setText(simpanan.getCreated_at());
        String format = formatRupiah(Double.parseDouble(String.valueOf(simpanan.getJumlah())));
        holder.txt_jumlah_simpanan.setText(format);
    }

    @Override
    public int getItemCount() {
        return dataSimpanans.size();
    }

    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DataSimpanan> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(dataSimpanans);
            } else {
                for (DataSimpanan getsimpanan : dataSimpanans) {
                    if (getsimpanan.getNo_simpanan().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getsimpanan.getMember_id().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getsimpanan.getCatatan().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getsimpanan.getCreated_at().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        searchList.add(getsimpanan);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = searchList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataSimpanans.clear();
            dataSimpanans.addAll((Collection<? extends DataSimpanan>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderData extends RecyclerView.ViewHolder {

        private TextView txt_member, txt_jumlah_simpanan, txt_id, txt_catatan, txt_tanggal;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            txt_id = itemView.findViewById(R.id.id);
            txt_member = itemView.findViewById(R.id.member_id);
            txt_jumlah_simpanan = itemView.findViewById(R.id.jmlh_pembayaran);
            txt_catatan = itemView.findViewById(R.id.catatan);
            txt_tanggal = itemView.findViewById(R.id.tanggal);
        }
    }
    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
