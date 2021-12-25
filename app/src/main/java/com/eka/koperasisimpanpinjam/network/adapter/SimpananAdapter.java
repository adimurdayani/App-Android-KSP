package com.eka.koperasisimpanpinjam.network.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.activity.DetailSimpananActivity;
import com.eka.koperasisimpanpinjam.network.model.DataSimpanan;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class SimpananAdapter extends RecyclerView.Adapter<SimpananAdapter.HolderData> {

    private Context context;
    private ArrayList<DataSimpanan> dataSimpanans;

    public SimpananAdapter(Context context, ArrayList<DataSimpanan> dataSimpanans) {
        this.context = context;
        this.dataSimpanans = dataSimpanans;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_data_home, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        DataSimpanan simpanan = dataSimpanans.get(position);

        holder.txt_id.setText(String.valueOf(simpanan.getId()));
        holder.txt_nomember.setText(simpanan.getMember_id());
        holder.txt_nosimpanan.setText(simpanan.getNo_simpanan());
        holder.txt_tanggal.setText(simpanan.getCreated_at());
        holder.txt_catatan.setText(simpanan.getCatatan());
        String format = formatRupiah(Double.parseDouble(String.valueOf(simpanan.getJumlah())));
        holder.txt_simpanan.setText(format);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailSimpananActivity.class);
                intent.putExtra("id", String.valueOf(simpanan.getId()));
                context.startActivity(intent);
            }
        });
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
        private TextView txt_tanggal, txt_simpanan, txt_nosimpanan, txt_nomember, txt_id, txt_catatan;
        RelativeLayout detail;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            txt_id = itemView.findViewById(R.id.id);
            txt_tanggal = itemView.findViewById(R.id.teks_tanggal);
            txt_simpanan = itemView.findViewById(R.id.simpanan);
            txt_nosimpanan = itemView.findViewById(R.id.no_simpanan);
            txt_catatan = itemView.findViewById(R.id.catatan);
            txt_nomember = itemView.findViewById(R.id.no_member);
            detail = itemView.findViewById(R.id.detail);

        }
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
