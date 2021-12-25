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
import com.eka.koperasisimpanpinjam.network.model.DataAngsuran;
import com.eka.koperasisimpanpinjam.network.model.DataSimpanan;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class AngsuranAdapter extends RecyclerView.Adapter<AngsuranAdapter.HolderData> {

    private Context context;
    private ArrayList<DataAngsuran> dataAngsurans;

    public AngsuranAdapter(Context context, ArrayList<DataAngsuran> dataAngsurans) {
        this.context = context;
        this.dataAngsurans = dataAngsurans;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_data_angsuran, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataAngsuran angsuran = dataAngsurans.get(position);

        holder.txt_id.setText(String.valueOf(angsuran.getId_a()));
        holder.txt_nomember.setText(angsuran.getMember_id());
        String format = formatRupiah(Double.parseDouble(String.valueOf(angsuran.getJumlah())));
        holder.txt_jmlpembayaran.setText(format);
        holder.txt_tanggal.setText(angsuran.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return dataAngsurans.size();
    }

    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DataAngsuran> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(dataAngsurans);
            } else {
                for (DataAngsuran angsuran : dataAngsurans) {
                    if (angsuran.getMember_id().toLowerCase().contains(constraint.toString().toLowerCase())
                            || angsuran.getCreated_at().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        searchList.add(angsuran);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = searchList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataAngsurans.clear();
            dataAngsurans.addAll((Collection<? extends DataAngsuran>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderData extends RecyclerView.ViewHolder {

        private TextView txt_id, txt_nomember, txt_jmlpembayaran, txt_tanggal;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            txt_id = itemView.findViewById(R.id.id);
            txt_nomember = itemView.findViewById(R.id.member_id);
            txt_jmlpembayaran = itemView.findViewById(R.id.jmlh_pembayaran);
            txt_tanggal = itemView.findViewById(R.id.tanggal);
        }
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
