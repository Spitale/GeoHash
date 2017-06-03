package i9.geohashme;

//Dependências
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import i9.geohashme.adapters.AdressesAdapter;
import i9.geohashme.models.Address;
import i9.geohashme.utils.Const;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnSair;
    private RecyclerView rviAddresses;
    private List<Address> addresses;//Lista de endereços
    private ImageView userPicture; //Image view para exibir imagem do usuário

    //FirebaseAuth
    private FirebaseAuth mAuth;

    //Primeiro método a ser chamado ao abrir a Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setUser();
        buscarEnderecos();
        setRecyclerView();
    }

    //Atribui dados do Usuario
    private void setUser() {
        String imageUrl = getIntent().getExtras().getString("profile_picture");//Imagem do Usuário, enviada pela tela de Login
        setToolbar(imageUrl);
    }

    //Recupera lista de Endereços
    private void buscarEnderecos() {
        addresses = Const.getAddresses(); //os dados são fixos, eles ficam no arquivo utils/Const

        //Posteriormente essa informação pode vir de uma API, ou do próprio Firebase.
    }

    //Recuperar os objetos com os ids colocados no layout
    private void findViews(){
        //Recupera instância do Firebase
        mAuth = FirebaseAuth.getInstance();
        btnSair = (Button) findViewById(R.id.btnSair);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rviAddresses = (RecyclerView) findViewById(R.id.rvi_addresses);
        userPicture = (ImageView) findViewById(R.id.profile_picture);
    }

    //Seta a imagem do usuário na toolbar
    private void setToolbar(String imageUrl){
        //Imagem do Usuario
        Picasso.with(MainActivity.this)
                .load(imageUrl)
                .placeholder(R.drawable.geohashlogo)
                .error(R.drawable.geohashlogo)
                .into(userPicture);
    }

    /**
     * Neste metodo o importante é criar o Adapter e falar pra ele qual é a lista com os dados a mostrar.
     * Também é possivel indicar uma ação cada vez que o usuário escolhe um item
     * **/
    private void setRecyclerView(){
        rviAddresses.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rviAddresses.setLayoutManager(mLayoutManager);

        AdressesAdapter addressesAdapter = new AdressesAdapter(addresses, MainActivity.this);//Criar o adapter e falar pra ele a lista de dados (endereços)
        addressesAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = rviAddresses.getChildLayoutPosition(view); //obter a posição do item que foi escolhido (0, 1, 2... n-1)
                Address addressSelecionado = addresses.get(position); //Recuperar o endereço selecionado
                openAddressInWaze(addressSelecionado.getLatitude(), addressSelecionado.getLongitude());
            }
        });

        rviAddresses.setAdapter(addressesAdapter);
        rviAddresses.setItemAnimator(new DefaultItemAnimator());
    }

    //Faz Logoff no sistema
    private void logOff() {
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class); //Navega para a activity "LoginActivity"

                mAuth.signOut();

                //Inicia activity
                startActivity(intent);
                finish();
            }
        });


    }

    //Abre aplicativo Waze, passando como parâmetro a Latitude e Longitude
    private void openAddressInWaze(String latitude, String longitude){
        String uri = "waze://?ll=" +latitude+ ", "+longitude+"&navigate=yes";
        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
    }
}



