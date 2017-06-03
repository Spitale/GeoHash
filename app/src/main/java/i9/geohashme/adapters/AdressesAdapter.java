package i9.geohashme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import i9.geohashme.R;
import i9.geohashme.models.Address;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdressesAdapter extends RecyclerView.Adapter<AdressesAdapter.ViewHolder>
        implements View.OnClickListener {

    /**
     * Não esqueca colocar as librerias no (app) build.gradle pra poder importar as clases
     *
     * Criar os objetos que vamos a utilizar
     * **/
    private List<Address> mData;//ele contem a data que vai ser mostrada no recycler view
    private View.OnClickListener listener;//ele simplesmente fala para o Activity que um item foi escolhido
    private Context context;//para poder utilizar o Picasso

    /**
     * Cosntrutor para obter as informações que precisamos
     * **/
    public AdressesAdapter(List<Address> myData, Context context) {
        this.mData = myData;
        this.context = context;
    }

    /**
     * Como é uma lista de objetos, cada objeto é como um Activity pequeno
     * então precisamos enlazar os views para ese objeto,
     * Simplesmente coloquem os view que foram utilizadas no layout (layout/song_layout.xml)
     * **/
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iviCover;
        TextView tviAddress;

        ViewHolder(View v) {
            super(v);
            iviCover = (ImageView) v.findViewById(R.id.ivi_cover);//enlazamos com o id colocado no layout
            tviAddress = (TextView) v.findViewById(R.id.tvi_address);
        }
    }

    /**
     * Com os objetos já enlazados, damos acções
     * **/
    @Override
    public AdressesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_layout, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder é o equivalente a 'setInfo()'  o metodo que a gente utilizava para colocar
     * informações nos views
     * **/
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String urlImage = mData.get(position).getUrlImagem() == null ? "" : mData.get(position).getUrlImagem();
        Picasso.with(context)
                .load(urlImage)
                .placeholder(R.drawable.geohashlogo)
                .error(R.drawable.geohashlogo)
                .into(holder.iviCover);

        holder.tviAddress.setText(mData.get(position).toString());
    }

    /**Pra saber o total de objetos que tem nossa lista**/
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /** damos acções, nem precisa mexer aqui **/
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    /** damos acções, nem precisa mexer aqui **/
    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(v);
    }
}