package com.tripbuddyc.service;

import com.tripbuddyc.model.Location;
import com.tripbuddyc.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    LocationRepository locationRepository;

    @Transactional
    public Location loadLocationByContinent(String continent) throws Exception {
        Location location = locationRepository.findByContinent(continent)
                .orElseThrow(() -> new Exception("Location Not Found with name: " + continent));

        return location;
    }

    @Transactional
    public void fillDatabaseWithCountries() {
        Location africa = new Location("Africa");
        Location asia = new Location("Asia");
        Location europe = new Location("Europe");
        Location northAmerica = new Location("North America");
        Location oceania = new Location("Oceania");
        Location southAmerica = new Location("South America");

        List<String> africaList = new ArrayList<>();
        List<String> asiaList = new ArrayList<>();
        List<String> europeList = new ArrayList<>();
        List<String> northAmericaList = new ArrayList<>();
        List<String> oceaniaList = new ArrayList<>();
        List<String> southAmericaList = new ArrayList<>();

        africaList.add("Algeria");
        africaList.add("Angola");
        africaList.add("Benin");
        africaList.add("Botswana");
        africaList.add("British Indian Ocean Territory");
        africaList.add("Burkina Faso");
        africaList.add("Burundi");
        africaList.add("Cabo Verde");
        africaList.add("Cameroon");
        africaList.add("Central African Republic");
        africaList.add("Chad");
        africaList.add("Comoros");
        africaList.add("Congo");
        africaList.add("Côte d’Ivoire");
        africaList.add("Democratic Republic of the Congo");
        africaList.add("Djibouti");
        africaList.add("Egypt");
        africaList.add("Equatorial Guinea");
        africaList.add("Eritrea");
        africaList.add("Eswatini");
        africaList.add("Ethiopia");
        africaList.add("French Southern Territories");
        africaList.add("Gabon");
        africaList.add("Gambia");
        africaList.add("Ghana");
        africaList.add("Guinea");
        africaList.add("Guinea-Bissau");
        africaList.add("Kenya");
        africaList.add("Lesotho");
        africaList.add("Liberia");
        africaList.add("Libya");
        africaList.add("Madagascar");
        africaList.add("Malawi");
        africaList.add("Mali");
        africaList.add("Mauritania");
        africaList.add("Mauritius");
        africaList.add("Mayotte");
        africaList.add("Morocco");
        africaList.add("Mozambique");
        africaList.add("Namibia");
        africaList.add("Niger");
        africaList.add("Nigeria");
        africaList.add("Réunion");
        africaList.add("Rwanda");
        africaList.add("Saint Helena");
        africaList.add("Sao Tome and Principe");
        africaList.add("Senegal");
        africaList.add("Seychelles");
        africaList.add("Sierra Leone");
        africaList.add("Somalia");
        africaList.add("South Africa");
        africaList.add("South Sudan");
        africaList.add("Sudan");
        africaList.add("Togo");
        africaList.add("Tunisia");
        africaList.add("Uganda");
        africaList.add("United Republic of Tanzania");
        africaList.add("Western Sahara");
        africaList.add("Zambia");
        africaList.add("Zimbabwe");

        asiaList.add("Afghanistan");
        asiaList.add("Armenia");
        asiaList.add("Azerbaijan");
        asiaList.add("Bahrain");
        asiaList.add("Bangladesh");
        asiaList.add("Bhutan");
        asiaList.add("Brunei Darussalam");
        asiaList.add("Cambodia");
        asiaList.add("China");
        asiaList.add("Hong Kong");
        asiaList.add("Macao");
        asiaList.add("Cyprus");
        asiaList.add("Democratic People's Republic of Korea");
        asiaList.add("Georgia");
        asiaList.add("India");
        asiaList.add("Indonesia");
        asiaList.add("Iran");
        asiaList.add("Iraq");
        asiaList.add("Israel");
        asiaList.add("Japan");
        asiaList.add("Jordan");
        asiaList.add("Kazakhstan");
        asiaList.add("Kuwait");
        asiaList.add("Kyrgyzstan");
        asiaList.add("Lao People's Democratic Republic");
        asiaList.add("Lebanon");
        asiaList.add("Malaysia");
        asiaList.add("Maldives");
        asiaList.add("Mongolia");
        asiaList.add("Myanmar");
        asiaList.add("Nepal");
        asiaList.add("Oman");
        asiaList.add("Pakistan");
        asiaList.add("Philippines");
        asiaList.add("Qatar");
        asiaList.add("Republic of Korea");
        asiaList.add("Saudi Arabia");
        asiaList.add("Singapore");
        asiaList.add("Sri Lanka");
        asiaList.add("State of Palestine");
        asiaList.add("Syrian Arab Republic");
        asiaList.add("Taiwan");
        asiaList.add("Tajikistan");
        asiaList.add("Thailand");
        asiaList.add("Timor-Leste");
        asiaList.add("Turkey");
        asiaList.add("Turkmenistan");
        asiaList.add("United Arab Emirates");
        asiaList.add("Uzbekistan");
        asiaList.add("Vietnam");
        asiaList.add("Yemen");

        europeList.add("Åland Islands");
        europeList.add("Albania");
        europeList.add("Andorra");
        europeList.add("Austria");
        europeList.add("Belarus");
        europeList.add("Belgium");
        europeList.add("Bosnia and Herzegovina");
        europeList.add("Bulgaria");
        europeList.add("Croatia");
        europeList.add("Czechia");
        europeList.add("Denmark");
        europeList.add("Estonia");
        europeList.add("Faroe Islands");
        europeList.add("Finland");
        europeList.add("France");
        europeList.add("Germany");
        europeList.add("Gibraltar");
        europeList.add("Greece");
        europeList.add("Guernsey");
        europeList.add("Holy See");
        europeList.add("Hungary");
        europeList.add("Iceland");
        europeList.add("Ireland");
        europeList.add("Isle of Man");
        europeList.add("Italy");
        europeList.add("Jersey");
        europeList.add("Latvia");
        europeList.add("Liechtenstein");
        europeList.add("Lithuania");
        europeList.add("Luxembourg");
        europeList.add("Malta");
        europeList.add("Monaco");
        europeList.add("Montenegro");
        europeList.add("Netherlands");
        europeList.add("North Macedonia");
        europeList.add("Norway");
        europeList.add("Poland");
        europeList.add("Portugal");
        europeList.add("Republic of Moldova");
        europeList.add("Romania");
        europeList.add("Russian Federation");
        europeList.add("San Marino");
        europeList.add("Sark");
        europeList.add("Serbia");
        europeList.add("Slovakia");
        europeList.add("Slovenia");
        europeList.add("Spain");
        europeList.add("Svalbard and Jan Mayen Islands");
        europeList.add("Sweden");
        europeList.add("Switzerland");
        europeList.add("Ukraine");
        europeList.add("United Kingdom of Great Britain and Northern Ireland");

        northAmericaList.add("Anguilla");
        northAmericaList.add("Antigua and Barbuda");
        northAmericaList.add("Aruba");
        northAmericaList.add("Bahamas");
        northAmericaList.add("Barbados");
        northAmericaList.add("Belize");
        northAmericaList.add("Bermuda");
        northAmericaList.add("Bonaire, Sint Eustatius and Saba");
        northAmericaList.add("British Virgin Islands");
        northAmericaList.add("Canada");
        northAmericaList.add("Cayman Islands");
        northAmericaList.add("Costa Rica");
        northAmericaList.add("Cuba");
        northAmericaList.add("Curaçao");
        northAmericaList.add("Dominica");
        northAmericaList.add("Dominican Republic");
        northAmericaList.add("El Salvador");
        northAmericaList.add("Greenland");
        northAmericaList.add("Grenada");
        northAmericaList.add("Guadeloupe");
        northAmericaList.add("Guatemala");
        northAmericaList.add("Haiti");
        northAmericaList.add("Honduras");
        northAmericaList.add("Jamaica");
        northAmericaList.add("Martinique");
        northAmericaList.add("Mexico");
        northAmericaList.add("Montserrat");
        northAmericaList.add("Nicaragua");
        northAmericaList.add("Panama");
        northAmericaList.add("Puerto Rico");
        northAmericaList.add("Saint Barthélemy");
        northAmericaList.add("Saint Kitts and Nevis");
        northAmericaList.add("Saint Lucia");
        northAmericaList.add("Saint Martin (French Part)");
        northAmericaList.add("Saint Pierre and Miquelon");
        northAmericaList.add("Saint Vincent and the Grenadines");
        northAmericaList.add("Sint Maarten (Dutch part)");
        northAmericaList.add("Trinidad and Tobago");
        northAmericaList.add("Turks and Caicos Islands");
        northAmericaList.add("United States of America");
        northAmericaList.add("United States Virgin Islands");

        oceaniaList.add("American Samoa");
        oceaniaList.add("Australia");
        oceaniaList.add("Christmas Island");
        oceaniaList.add("Cocos (Keeling) Islands");
        oceaniaList.add("Cook Islands");
        oceaniaList.add("Fiji");
        oceaniaList.add("French Polynesia");
        oceaniaList.add("Guam");
        oceaniaList.add("Heard Island and McDonald Islands");
        oceaniaList.add("Kiribati");
        oceaniaList.add("Marshall Islands");
        oceaniaList.add("Micronesia (Federated States of)");
        oceaniaList.add("Nauru");
        oceaniaList.add("New Caledonia");
        oceaniaList.add("New Zealand");
        oceaniaList.add("Niue");
        oceaniaList.add("Norfolk Island");
        oceaniaList.add("Northern Mariana Islands");
        oceaniaList.add("Palau");
        oceaniaList.add("Papua New Guinea");
        oceaniaList.add("Pitcairn");
        oceaniaList.add("Samoa");
        oceaniaList.add("Solomon Islands");
        oceaniaList.add("Tokelau");
        oceaniaList.add("Tonga");
        oceaniaList.add("Tuvalu");
        oceaniaList.add("United States Minor Outlying Islands");
        oceaniaList.add("Vanuatu");
        oceaniaList.add("Wallis and Futuna Islands");

        southAmericaList.add("Argentina");
        southAmericaList.add("Bolivia ");
        southAmericaList.add("Bouvet Island");
        southAmericaList.add("Brazil");
        southAmericaList.add("Chile");
        southAmericaList.add("Colombia");
        southAmericaList.add("Ecuador");
        southAmericaList.add("Falkland Islands (Malvinas)");
        southAmericaList.add("French Guiana");
        southAmericaList.add("Guyana");
        southAmericaList.add("Paraguay");
        southAmericaList.add("Peru");
        southAmericaList.add("South Georgia and the South Sandwich Islands");
        southAmericaList.add("Suriname");
        southAmericaList.add("Uruguay");
        southAmericaList.add("Venezuela");

        africa.setCountries(africaList);
        asia.setCountries(asiaList);
        europe.setCountries(europeList);
        northAmerica.setCountries(northAmericaList);
        oceania.setCountries(oceaniaList);
        southAmerica.setCountries(southAmericaList);

        locationRepository.save(africa);
        locationRepository.save(asia);
        locationRepository.save(europe);
        locationRepository.save(northAmerica);
        locationRepository.save(oceania);
        locationRepository.save(southAmerica);
    }
}
