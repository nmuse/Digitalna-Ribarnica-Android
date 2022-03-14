package com.example.badges;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.database.User;

import java.lang.annotation.Repeatable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Logic {

    BadgesRepository badgesRepository = new BadgesRepository();
    private User user;
    ArrayList<BadgeID> badgesIDList;
    ArrayList<BadgesData> badgesList;
    private Context reservationFragmentContext;
    private String category;
    private Integer BRKUPNJI = 0;
    private Integer BRPRODAJA = 0;
    private Integer POSTOJI = 0;
    private float OCJENAF;
    private Integer OCJENA;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Logic(User user, ArrayList<BadgesData> badges, ArrayList<BadgeID> badgesID, Context reservationFragmentContext, String category) {
        this.user = user;
        this.badgesList = badges;
        this.badgesIDList = badgesID;
        this.reservationFragmentContext = reservationFragmentContext;
        this.category = category;
        InicijalizirajVarijabe();
        postaviPOSTOJI(badges, badgesID, category);
    }

   /*@RequiresApi(api = Build.VERSION_CODES.O)
    public Logic() {
        PretvoriUPostfix();
    }*/


    private void InicijalizirajVarijabe(){
      BRKUPNJI = user.getNumberOfPurchases();
      BRPRODAJA = user.getNumberOfSales();
      OCJENAF = user.getUserRating();
      int zadnjaZnamenka = (int)((OCJENAF*10)%10);
      if(zadnjaZnamenka > 4) {
          OCJENA = (int)OCJENAF+1;
      }
      else {
          OCJENA = (int)OCJENAF;
      }
      Log.d("ocjena", String.valueOf(OCJENA));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String PretvoriUPostfix(String condition, BadgesData badge, String category){
        condition = ZamijeniVarijable(condition);

        ArrayList<String> allConditions = new ArrayList<>();
        allConditions.add(condition);
        String output = "";
        ArrayList<String> allOutputs = new ArrayList<>();

        Boolean evaluationResult = false;
        ArrayList<Boolean> allEvaluationResults = new ArrayList<>();

        for (String c: allConditions) {
            Log.d("vidi2", c);
            output = InfixToPostfix.calculateInfixToPostfix(condition);
            output = String.join(" ", output);
            allOutputs.add(output);
        }

        for (String o: allOutputs) {
            Log.d("vidi3", o);
            evaluationResult = ProvjeriIstinitost(o, badge, category);
            allEvaluationResults.add(evaluationResult);
        }

        for (Boolean e: allEvaluationResults) {
            Log.d("vidi4", e.toString());
        }

        //String[] conditionArray = condition.split(" ");

        //List<String> output = InfixToPostfix.infixToRPN(conditionArray);
        //Log.d("TagPolje", conditionArray.toString());

        //String stringCondition = String.join(" ", conditionArray);

        //Log.d("testiraj1", stringCondition);
        //String output2 = InfixToPostfix.calculateInfixToPostfix(infixCondition);
        //String output = InfixToPostfix.calculateInfixToPostfix("(10>9)&(0>0)");
        //Log.d("vidi", output2);


        //String stringCondition2 = String.join(" ", output);
        //Integer result = (CalculatePostfix.evaluatePostfix(stringCondition2));
        //Log.d("vidi", String.valueOf(result));

        //ProvjeriIstinitost(stringCondition, badge, category);

        return output;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    public void PretvoriUPostfix(){
        //condition = ZamijeniVarijable(condition);
        //String[] conditionArray = condition.split(" ");
        //List<String> output = InfixToPostfix.infixToRPN(conditionArray);
        //Log.d("TagPolje", output.toString());

        //String stringCondition = String.join(" ", conditionArray);

        //Log.d("testiraj1", stringCondition);
        //String output = InfixToPostfix.calculateInfixToPostfix(infixCondition);
        //(300>29)&(0=0)
        String output = InfixToPostfix.calculateInfixToPostfix("(20>29)&(0=0)");
        Log.d("vidii", output);


        String stringCondition2 = String.join(" ", output);
        Integer result = (CalculatePostfix.evaluatePostfix(stringCondition2));
        Log.d("vidii", String.valueOf(result));
    }*/

    private String ZamijeniVarijable(String condition) {
        if(condition.contains("BRKUPNJI")){
            condition = condition.replace("BRKUPNJI", BRKUPNJI.toString());
        }
        if(condition.contains("BRPRODAJA")){
            condition = condition.replace("BRPRODAJA", BRPRODAJA.toString());
        }
        if(condition.contains("OCJENA")){
            condition = condition.replace("OCJENA", String.valueOf(OCJENA));
        }
        if(condition.contains("==")){
            condition = condition.replace("==","=");
        }
        if(condition.contains("||")){
            condition = condition.replace("||", "|");
        }
        if(condition.contains("&&")){
            condition = condition.replace("&&", "&");
        }
        return condition;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean ProvjeriIstinitost(String condition, BadgesData badge, String category){
        Integer result = (CalculatePostfix.evaluatePostfix(condition));

        Boolean returnResult;

        if(result == 1) {
            returnResult = true;
        }
        else {
            returnResult = false;
        }

        if(result==1) {
            if (category.contains("quiz")) {
                badgesRepository.DohvatiPitanjaZaKviz(new QuizCallBack() {
                    @Override
                    public void onCallback(ArrayList<QuizData> quizData) {
                        CustomDialogBadgeQuiz customDialogBadgeQuiz = new CustomDialogBadgeQuiz();
                        customDialogBadgeQuiz.setContexPrikazivanja(reservationFragmentContext);
                        customDialogBadgeQuiz.setData(user, badge);
                        customDialogBadgeQuiz.setQuestions(quizData);
                        try {
                            customDialogBadgeQuiz.prikaziDialogKorisniku();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                CustomDialogBadge customDialogBadge = new CustomDialogBadge();
                customDialogBadge.setContexPrikazivanja(reservationFragmentContext);
                customDialogBadge.setData(user, badge);
                customDialogBadge.izvrsiUpdateKorisnika();
                customDialogBadge.prikaziDialogKorisniku();
            }
        }
        return returnResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void postaviPOSTOJI(ArrayList<BadgesData> badges, ArrayList<BadgeID> badgesID, String category) {
        boolean exists;
        Log.d("TagPolje", "postaviPOSTOJI: ");
        for (int i = 0; i < badges.size(); i++) {
            exists = true;
            if(badgesID.size() == 0){
                if (category.contains(badges.get(i).getCategory())) {
                    POSTOJI = 0;
                    Log.d("TagPolje", "1NEPOSTOJI: ");
                    exists = false;

                }else{
                    POSTOJI = 1;
                    Log.d("TagPolje", "1POSTOJI: ");
                    exists = true;
                }
            }
            else{
                for (int j = 0; j < badgesID.size(); j++) {
                    if ((!(badges.get(i).getBadgeID().equals(badgesID.get(j).getId()))) && category.contains(badges.get(i).getCategory())) {
                        POSTOJI = 0;
                        Log.d("TagPolje", "2NEPOSTOJI: ");
                        exists = false;

                    }else{
                        POSTOJI = 1;
                        Log.d("TagPolje", "2POSTOJI: ");
                        exists = true;
                        break;
                    }
                }
            }

            if(!exists){
                String condition = badges.get(i).getCondition();
                if(condition.contains("POSTOJI")){
                    condition = condition.replace("POSTOJI", POSTOJI.toString());
                }
                PretvoriUPostfix(condition, badges.get(i), badges.get(i).getCategory());
            }
        }
    }

}
