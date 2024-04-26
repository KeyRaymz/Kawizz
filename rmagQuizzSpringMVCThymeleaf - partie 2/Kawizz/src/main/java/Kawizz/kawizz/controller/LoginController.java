package Kawizz.kawizz.controller;

import Kawizz.kawizz.model.JsonEntryPoint;
import Kawizz.kawizz.service.ListeQuestions;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;

@Controller
@SessionAttributes({"pseudo", "index","quizz", "score"})
public class LoginController {

    @Autowired
    ListeQuestions listequest;


    @GetMapping("/connexion")
    public String afficherMonAccueil() {
        return "index";
    }


    @GetMapping("/login")
    public String validerSonLogin(HttpSession session, Model model, @RequestParam String pseudo) {

        //Déclaration et initialisation d'un index si non existant dans l'objet session:
        int index = 0;

        int playerScore = 0;


        //Stockage en modèle et en session des différents attributs:

        //On crée d'autres attributs dans model pour les variables
        // listequest et index:

        ArrayList<String> mesQuestion = new ArrayList<>();
        ArrayList<ArrayList<String>> mesListesDeReponses = new ArrayList<>();

        JsonEntryPoint operator = listequest.retreiveList();

        session.setAttribute("quizz",operator);
        session.setAttribute("pseudo",pseudo);
        session.setAttribute("index",0);
        session.setAttribute("score",0);
        session.setAttribute("tailleListe",operator.getQuizzes().size());




        /*Ici on récupère le pseudo entré par l'utilisateur
        via le paramètre @RequestParam
        et on lui crée un attribut correspondant dans l'objet model:*/
        model.addAttribute("score", playerScore);
        model.addAttribute("pseudo", pseudo);
        model.addAttribute("index", index);
        model.addAttribute("tailleListe", operator.getQuizzes().size());



        String questionEnCours = operator.getQuizzes().get(index).getQuestion();
        model.addAttribute("questionencours", questionEnCours);

        //Gestion de l'initialisation du Quizz - 1ère question / réponses possibles:
        String maBonneReponseQuestion = operator.getQuizzes().get(index).getAnswer();
        ArrayList<String> reponsesQuestionsEnCours = new ArrayList<>();
        reponsesQuestionsEnCours.add(maBonneReponseQuestion);
        for (String badAnswers : operator.getQuizzes().get(index).getBadAnswers()) {
            reponsesQuestionsEnCours.add(badAnswers);
        }
        Collections.shuffle(reponsesQuestionsEnCours);
        model.addAttribute("reponsesQuestion", reponsesQuestionsEnCours);

//

//        model.addAttribute("bonnRepQuestion", maBonneReponseQuestion);
//
//
//        int tailleListe = listequest.retreiveList().getQuizzes().size();
//        model.addAttribute("tailleListe", tailleListe);

        return "quizz";


    }


    @RequestMapping(value = "/quizz", method = RequestMethod.POST)
    public String progressionQuestions(Model model, HttpSession session, @RequestParam String repuser) {


        JsonEntryPoint operator = (JsonEntryPoint)session.getAttribute("quizz");

        int index = (int)session.getAttribute("index");


        if (index + 1 < (int) session.getAttribute("tailleListe")) {

            model.addAttribute("index", index);
            int score = (int)session.getAttribute("score");


            if (operator.getQuizzes().get(index).getAnswer().equals(repuser)) {
                score++;
                System.out.println(score);
                session.setAttribute("score", score);
            }

            index++;
            session.setAttribute("index", index );

            model.addAttribute("score", score);
            model.addAttribute("pseudo", session.getAttribute("pseudo"));
            model.addAttribute("index", index);
            model.addAttribute("tailleListe", operator.getQuizzes().size());



            String questionEnCours = operator.getQuizzes().get(index).getQuestion();
            model.addAttribute("questionencours", questionEnCours);

            //Gestion de l'initialisation du Quizz - 1ère question / réponses possibles:
            String maBonneReponseQuestion = operator.getQuizzes().get(index).getAnswer();
            ArrayList<String> reponsesQuestionsEnCours = new ArrayList<>();
            reponsesQuestionsEnCours.add(maBonneReponseQuestion);
            for (String badAnswers : operator.getQuizzes().get(index).getBadAnswers()) {
                reponsesQuestionsEnCours.add(badAnswers);


            }
            Collections.shuffle(reponsesQuestionsEnCours);
            model.addAttribute("reponsesQuestion", reponsesQuestionsEnCours);


            return "quizz";

        }

        model.addAttribute("finalScore", session.getAttribute("score"));
        return "results";
    }}