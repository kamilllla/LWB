package com.example.lwb;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void numbers_isCorrect() {

        List<String> listOfCorrectsNumbers= Arrays.asList("89119553535",
                "+7(911)95535-35",
                "+7(911)955-3535",
                "+7911)955-35-35",
                "+7(911)955-35-35",
        "8(911)955-35-35",
                "+7(911955-35-35",
                "+79119553535",
        "89669552003",
                "+79668555200",
       " 89214397275");

        for (String s:listOfCorrectsNumbers) {

            assertEquals(false, VerificationAndValidation.checkNumberIncorrect(s));
        }
    }
    @Test
    public void numbers_isIncorrect() {

        List<String> listOfIncorrectsNumbers= Arrays.asList("+7(811)955-35-35"," hvjhvjjj",
                "+8(911)955-35-35",
                "+7(911)-955-35-35",
                "+79116955-35-35",
                "+89119553535",
                "9119553535",
                " dfghjkl",
                "76543212345688");
        for (String s:listOfIncorrectsNumbers) {

            assertEquals(true, VerificationAndValidation.checkNumberIncorrect(s));
        }


    }
    @Test
    public void replaceInNumber() {
        String number="+7921) 974-3275";

        //number = number.replaceAll("[\\-\\(\\)]", "");
        assertEquals("+7921 9743275", number.replaceAll("[\\-\\(\\)]", ""));

    }
    @Test
    public void email_isCorrect() {

        List<String> listOfCorrectsEmails= Arrays.asList("agafonov.ayu@ssau.ru","aspir@ssau.ru","antonevich.an@ssau.ru","kipres@ssau.ru","arkhipov@ssau.ru","aslanov_vs@mail.ru","aslanov@ssau.ru",
                "byui@ssau.ru","balabaeva.yuv@ssau.ru","balykin@ssau.ru","okm@ssau.ru",
                "bezrukova.aa@ssau.ru","acad@ssau.ru","aibelousov@mail.ru","teplotex@ssau.ru",
                "pbir@ssau.ru","blagov@ssau.ru","blatov@samsu.ru","rector@ssau.ru","vbogd@ssau.ru",
                "aspir@ssau.ru","bogdana@ssau.ru","museum@ssau.ru ", "bogdanovich@ssau.ru ",
                "bolgova.vv@ssau.ru","boyarkina.uv@ssau.ru","career@ssau.ru ","abukhanko@mail.ru",
                "usovet@ssau.ru ","vashukov@ssau.ru","nirs@ssau.ru","veshagina@ssau.ru","voronina.na@ssau.ru ",
                "voronov@ssau.ru ","voronova.ov@ssau.ru","gavrilov@ssau.ru","rud@ssau.ru ","gareyev@ssau.ru",
                "mat-met@ssau.ru ","nglu@geosamara.ru ","vgl@ssau.ru","bagor@ssau.ru ","gorojankina.ia@ssau.ru ",
                "gsb@ssau.ru","gv@ssau.ru ","gretch@ssau.ru","grishanov@ssau.ru","aidan@ssau.ru ","dolganova.ov@sau.ru",
                "dolgich@ssau.ru","dorosh@ssau.ru","elenev@ssau.ru","ssau@ssau.ru",
               " email@example.com",
        "firstname.lastname@example.com",
       " email@subdomainexample.com",
        "firstnamelastname@example.com",
       " email@example.com",
        "1234567890@example.com",
        "email@exampleone.com",
        "h_f@example.com",
        "email@example.name",
        "email@example.museum",
        "email@example.jp",
       " firstname-lastname@example.com",
                "kamilla-balaeva@mail.ru");


        for (String s:listOfCorrectsEmails) {


            assertEquals(false, VerificationAndValidation.checkEmailIncorrect(s));
        }


    }
    @Test
    public void email_isIncorrect() {

        List<String> listOfIncorrectsEmails= Arrays.asList("plainaddress","#@%^%#$@#$@#.com","@example.com",
                "Joe Smith <email@example.com>","email.example.com","email@example@example.com","email.@example.com","あいうえお@example.com","email@example.com (Joe Smith)","email@example","email@-example.com","email@111.222.333.44444",
                ".email@example.com"," (),:;<>[]@example.com","just not right@example.com","this/ is/ really/ not allowed@example.com",
                ".email@example.com",
                "email..email@example.com"
               ,"h_____f@example.com",
        "email@example..com",
        "Abc..123@example.com",
        "Abc..123@example.com");


        for (String s:listOfIncorrectsEmails) {


            assertEquals(true, VerificationAndValidation.checkEmailIncorrect(s));
        }


    }


    @Test
    public void fio_isIncorrect() {
        List<String> listOfIncorrectsEmails= Arrays.asList("plaina ddress","#@%^%#$@#$@#.com","@example.com",
                "Joe Smith <email@example.com>","email.example.com","email@example@example.com","email.@example.com","あいうえお@example.com","email@example.com (Joe Smith)","email@example","email@-example.com","email@111.222.333.44444",
                ".email@example.com"," (),:;<>[]@example.com","just not right@example.com","this/ is/ really/ not allowed@example.com",
                ".email@example.com",
                "email..email@example.com"
                ,"h_____f@example.com",
                "email@example..com",
                "Abc..123@example.com",
                "Abc..123@example.com", "123456789", "trstr ytfytfyt", "g","dtrdtr tdytd", "    `");


        for (String s:listOfIncorrectsEmails) {

            assertEquals(true, VerificationAndValidation.checkFIOIncorrect(s));
        }


    }


    @Test
    public void fio_isCorrect() {
        List<String> listOfIncorrectsEmails= Arrays.asList("plaina","Балаева","Rфьшддф","Ян");


        for (String s:listOfIncorrectsEmails) {

            assertEquals(false, VerificationAndValidation.checkFIOIncorrect(s));
        }


    }



}