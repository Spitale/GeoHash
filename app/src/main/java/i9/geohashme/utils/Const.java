package i9.geohashme.utils;

import i9.geohashme.models.Address;

import java.util.ArrayList;
import java.util.List;

public class Const {

    /**
     * Criamos uma lista de objetos (models/Address) para mandar ao Adapter (adapters/AdressesAdapter)
     * **/
    public static List<Address> getAddresses(){
        List<Address> addresses = new ArrayList<>();

        //Address(id, logradouro, numero, complemento, bairro, cidade, uf, cep, latitude, longitude, urlImagem, hash)
        addresses.add(new Address("1", "Rua Senegalia", "134", "Sobrado 05", "Atuba", "Colombo", "PR", "83413250", "-25.3902887", "-49.1881234", "http://affordablecomfort.org/wp-content/uploads/2017/01/family-home-475883_1280.jpg", "MinhaCasa"));
        addresses.add(new Address("2", "Rua Doutor Roberto Barrozo", "307", "Sala 25", "Centro Cívico", "Curitiba", "PR", "80520070", "-25.4181828", "-49.2742318", "http://www.brco.com.br/wp-content/uploads/2014/05/brco_web2.png", "MeuTrabalho"));
        addresses.add(new Address("3", "R. Engo. Ostoja Roguski", "", "", "Jardim Botânico", "Curitiba", "PR", "80210390", "-25.4420753", "-49.2409591", "https://c1.staticflickr.com/9/8081/8301548170_52ca05e38f_b.jpg", "JardimBotanico"));

        return addresses;
    }

}
