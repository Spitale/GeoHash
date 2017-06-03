package i9.geohashme;

//Dependências
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import i9.geohashme.models.Address;
import i9.geohashme.models.User;

public class LoginActivity extends AppCompatActivity {


    public User user; //Usuário

    //FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Referência ao Firebase
    Firebase mRef = new Firebase("https://geohashme.firebaseio.com/users/");

    private ProgressDialog mProgressDialog; //Loading enquanto processa
    private CallbackManager callbackManager; //Facebook Callback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO:Spitale - criar métodos para realizar ações

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Recupera instância do Firebase
        mAuth = FirebaseAuth.getInstance();

        //Recupera usuário logado no FireBase (Facebook)
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) { //Usuário está Logado
            Intent intent = new Intent(getApplicationContext(), MainActivity.class); //Navega para a activity "MainActivity"

            //Recupera dados do usuário logado do Facebook (UID, Imagem do Perfil)
            String uid = mAuth.getCurrentUser().getUid();
            String image=mAuth.getCurrentUser().getPhotoUrl().toString();

            //Insere Extras (UID, Imagem do Perfil) no Intent para recuperar os dados na activity MainActivity
            intent.putExtra("user_id", uid);
            if(image!=null || image!=""){
                intent.putExtra("profile_picture",image);
            }

            //Inicia activity
            startActivity(intent);
            finish();
        }

        //Cria Listener, para ser avisado sempre que o estado da autenticação mudar no Firebase
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser(); //Recupera usuário logado no FireBase (Facebook)

                if (mUser != null) { //Usuário está Logado
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class); //Navega para a activity "MainActivity"

                    //Recupera dados do usuário logado do Facebook (UID, Imagem do Perfil)
                    String uid = mAuth.getCurrentUser().getUid();
                    String image=mAuth.getCurrentUser().getPhotoUrl().toString();

                    //Insere Extras (UID, Imagem do Perfil) no Intent para recuperar os dados na activity MainActivity
                    intent.putExtra("user_id", uid);
                    if(image!=null || image!=""){
                        intent.putExtra("profile_picture",image);
                    }

                    //Inicia activity
                    startActivity(intent);
                    finish();
                } else {
                    //Usuário não está Logado
                    firebaseAuth.signOut();
                }
            }
        };

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton); //Botão de Login do Facebook
        loginButton.setReadPermissions("email", "public_profile"); //Define as permissões que o usuário deve conceder, para ter acesso ao app

        //Registra Callback
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                signInWithFacebook(loginResult.getAccessToken()); //Caso login seja feito com sucesso (facebook)
            }

            @Override
            public void onCancel() {
                //Se o usuário cancelar
                Toast.makeText(LoginActivity.this, "Autenticação Cancelada.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                //Erro na autenticação
                Toast.makeText(LoginActivity.this, "Erro: - " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
   }

    @Override
    protected void onStart() {
        super.onStart();
        //Adicionar listener de autenticação
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //Remover listener de autenticação
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //FaceBook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Método para realizar login com Facebook
    private void signInWithFacebook(AccessToken token) {
        showProgressDialog(); //Exibe indicação que está carregando

        //Login com facebook
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //Exibe mensagem Toast, em caso de erro na autenticação
                            Toast.makeText(LoginActivity.this, "Autenticação Falhou.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Sucesso na autenticação. Recupera os dados do usuário logado
                            String uid = task.getResult().getUser().getUid();
                            String name = task.getResult().getUser().getDisplayName();
                            String email = task.getResult().getUser().getEmail();
                            String image = task.getResult().getUser().getPhotoUrl().toString();

                            //Cria novo objeto Usuario, e persiste na base Firebase
                            User user = new User(uid, name, email);
                            mRef.child(uid).setValue(user); //Salvando um item na lista de usuários. recupera o child node UID e seta o valor

                            //Navega para a activity "MainActivity"
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            //Insere Extras (UID, Imagem do Perfil) no Intent para recuperar os dados na activity MainActivity
                            intent.putExtra("user_id", uid);
                            intent.putExtra("profile_picture", image);

                            //Inicia activity
                            startActivity(intent);
                            finish();
                        }
                        hideProgressDialog(); //Esconde modal de progresso.
                    }
                });
    }


    //Método para exibir modal de progresso
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    //Método para esconder modal de progresso
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}
