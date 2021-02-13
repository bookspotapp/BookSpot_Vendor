package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    EditText  owner_name, contact_no;
    TextView send_otp;
    Spinner spinner;
    public static String  o_name,  c_no, code;

    public static final String[] countryNames = { "Afghanistan", "Albania",
            "Algeria", "Andorra", "Angola", "Antarctica", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
            "Bahrain", "Bangladesh", "Belarus", "Belgium", "Belize", "Benin",
            "Bhutan", "Bolivia", "Bosnia And Herzegovina", "Botswana",
            "Brazil", "Brunei Darussalam", "Bulgaria", "Burkina Faso",
            "Myanmar", "Burundi", "Cambodia", "Cameroon", "Canada",
            "Cape Verde", "Central African Republic", "Chad", "Chile", "China",
            "Christmas Island", "Cocos (keeling) Islands", "Colombia",
            "Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia",
            "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti",
            "Timor-leste", "Ecuador", "Egypt", "El Salvador",
            "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",
            "Falkland Islands (malvinas)", "Faroe Islands", "Fiji", "Finland",
            "France", "French Polynesia", "Gabon", "Gambia", "Georgia",
            "Germany", "Ghana", "Gibraltar", "Greece", "Greenland",
            "Guatemala", "Guinea", "Guinea-bissau", "Guyana", "Haiti",
            "Honduras", "Hong Kong", "Hungary", "India", "Indonesia", "Iran",
            "Iraq", "Ireland", "Isle Of Man", "Israel", "Italy", "Ivory Coast",
            "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati",
            "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho",
            "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
            "Macao", "Macedonia", "Madagascar", "Malawi", "Malaysia",
            "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania",
            "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
            "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique",
            "Namibia", "Nauru", "Nepal", "Netherlands", "New Caledonia",
            "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Korea",
            "Norway", "Oman", "Pakistan", "Palau", "Panama",
            "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn",
            "Poland", "Portugal", "Puerto Rico", "Qatar", "Romania",
            "Russian Federation", "Rwanda", "Saint Barthélemy", "Samoa",
            "San Marino", "Sao Tome And Principe", "Saudi Arabia", "Senegal",
            "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia",
            "Slovenia", "Solomon Islands", "Somalia", "South Africa",
            "Korea, Republic Of", "Spain", "Sri Lanka", "Saint Helena",
            "Saint Pierre And Miquelon", "Sudan", "Suriname", "Swaziland",
            "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan",
            "Tajikistan", "Tanzania", "Thailand", "Togo", "Tokelau", "Tonga",
            "Tunisia", "Turkey", "Turkmenistan", "Tuvalu",
            "United Arab Emirates", "Uganda", "United Kingdom", "Ukraine",
            "Uruguay", "United States", "Uzbekistan", "Vanuatu",
            "Holy See (vatican City State)", "Venezuela", "Viet Nam",
            "Wallis And Futuna", "Yemen", "Zambia", "Zimbabwe" };

    public static final String[] countryAreaCodes = { "93", "355",
            "213", "376", "244", "672", "54",
            "374", "297", "61", "43", "994",
            "973", "880", "375", "32", "501",
            "229", "975", "591", "387", "267",
            "55", "673", "359", "226", "95",
            "257", "855", "237", "1", "238",
            "236", "235", "56", "86", "61",
            "61", "57", "269", "242", "682",
            "506", "385", "53", "357", "420",
            "45", "253", "670", "593", "20",
            "503", "240", "291", "372", "251",
            "500", "298", "679", "358", "33",
            "689", "241", "220", "995", "49",
            "233", "350", "30", "299", "502",
            "224", "245", "592", "509", "504",
            "852", "36", "91", "62", "98",
            "964", "353", "44", "972", "39",
            "225", "1876", "81", "962", "7",
            "254", "686", "965", "996", "856",
            "371", "961", "266", "231", "218",
            "423", "370", "352", "853", "389",
            "261", "265", "60", "960", "223",
            "356", "692", "222", "230", "262",
            "52", "691", "373", "377", "976",
            "382", "212", "258", "264", "674",
            "977", "31", "687", "64", "505",
            "227", "234", "683", "850", "47",
            "968", "92", "680", "507", "675",
            "595", "51", "63", "870", "48",
            "351", "1", "974", "40", "7",
            "250", "590", "685", "378", "239",
            "966", "221", "381", "248", "232",
            "65", "421", "386", "677", "252",
            "27", "82", "34", "94", "290",
            "508", "249", "597", "268", "46",
            "41", "963", "886", "992", "255",
            "66", "228", "690", "676", "216",
            "90", "993", "688", "971", "256",
            "44", "380", "598", "1", "998",
            "678", "39", "58", "84", "681",
            "967", "260", "263" };

    public static final String[] countryCodes = { "AF", "AL", "DZ", "AD", "AO",
            "AQ", "AR", "AM", "AW", "AU", "AT", "AZ", "BH", "BD", "BY", "BE",
            "BZ", "BJ", "BT", "BO", "BA", "BW", "BR", "BN", "BG", "BF", "MM",
            "BI", "KH", "CM", "CA", "CV", "CF", "TD", "CL", "CN", "CX", "CC",
            "CO", "KM", "CG", "CK", "CR", "HR", "CU", "CY", "CZ", "DK", "DJ",
            "TL", "EC", "EG", "SV", "GQ", "ER", "EE", "ET", "FK", "FO", "FJ",
            "FI", "FR", "PF", "GA", "GM", "GE", "DE", "GH", "GI", "GR", "GL",
            "GT", "GN", "GW", "GY", "HT", "HN", "HK", "HU", "IN", "ID", "IR",
            "IQ", "IE", "IM", "IL", "IT", "CI", "JM", "JP", "JO", "KZ", "KE",
            "KI", "KW", "KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT",
            "LU", "MO", "MK", "MG", "MW", "MY", "MV", "ML", "MT", "MH", "MR",
            "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MA", "MZ", "NA",
            "NR", "NP", "NL", "NC", "NZ", "NI", "NE", "NG", "NU", "KP", "NO",
            "OM", "PK", "PW", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT",
            "PR", "QA", "RO", "RU", "RW", "BL", "WS", "SM", "ST", "SA", "SN",
            "RS", "SC", "SL", "SG", "SK", "SI", "SB", "SO", "ZA", "KR", "ES",
            "LK", "SH", "PM", "SD", "SR", "SZ", "SE", "CH", "SY", "TW", "TJ",
            "TZ", "TH", "TG", "TK", "TO", "TN", "TR", "TM", "TV", "AE", "UG",
            "GB", "UA", "UY", "US", "UZ", "VU", "VA", "VE", "VN", "WF", "YE",
            "ZM", "ZW",

    };

    List<String> finalCodes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //createNotificationChannel();
        initialize();

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_name = owner_name.getText().toString();
                c_no = contact_no.getText().toString();

                if(o_name.isEmpty())
                    owner_name.setError("Enter the owner name");
                if(c_no.isEmpty() || c_no.length() != 10)
                    contact_no.setError("Enter the valid Contact No.");
                if(spinner.getSelectedItemPosition() == 0)
                    Toast.makeText(Register.this, "Please! Select Country Code", Toast.LENGTH_SHORT).show();

                if(!o_name.isEmpty() && isPhoneNumberCorrect(c_no) && spinner.getSelectedItemPosition() != 0){
                    c_no = "+"+ countryAreaCodes[spinner.getSelectedItemPosition() - 1] + c_no;
                    startActivity(new Intent(Register.this, Verification.class));
                    finish();
                }
            }
        });
    }

    private void initialize() {
        owner_name = (EditText) findViewById(R.id.owner_name);
        contact_no = (EditText) findViewById(R.id.contact_no);
        send_otp = (TextView) findViewById(R.id.send_otp);

        finalCodes = new ArrayList<>();
        finalCodes.add("Select Country Code");
        for(int i=0;i<countryAreaCodes.length;i++){
            finalCodes.add( "+" + countryAreaCodes[i] + "  " + countryNames[i] + " ( " + countryCodes[i] + " )");
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,finalCodes);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    private boolean isPhoneNumberCorrect(String phone) {
        return Pattern.matches("[0-9]{10}", phone);
    }
/*
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Booking Alert";
            String description = "New Booking";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(SplashScreen.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[] { 1000, 1500, 1000, 1500, 1000 });
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


 */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Register.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.orange));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.orange));
            }
        });
        dialog.show();
    }


}