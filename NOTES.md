# compta-api — notes de TP

Une section par TP, dans l'ordre : `## TP 01`, `## TP 02`… puis `## Projet`.

Une prédiction s'écrit **avant** d'exécuter, et ne se corrige pas après : on écrit l'écart en dessous.
Une sortie se copie telle que le terminal l'affiche.


#TP : 

2 - Copie de la ligne Stated... : `2026-11-24T14:51:12.334+01:00  INFO 25994 --- [compta-api] [           main] org.sebsy.compta.ComptaApiApplication    : Started ComptaApiApplication in 1.305 seconds (process running for 1.428)
` Voici le résultat après avaoir lancé l'application.

3 - Intérrogation de l'état de santé de l'application : `{"groups":["liveness","readiness"],"status":"UP"}%` : Le `UP` signifie que l'application est opérationnel 

4 - La base est stoppée et quand je test `curl -i localhost:8081/actuator/health` j'ai : 
`
HTTP/1.1 503
Content-Type: application/vnd.spring-boot.actuator.v3+json
Transfer-Encoding: chunked
Date: Tue, 24 Nov 2026 13:59:35 GMT
Connection: close

{"groups":["liveness","readiness"],"status":"DOWN"}%
`

Et le status code 503 : Service indisponible

on vois bien le statut `DOWN` et Connection closed, ce qui veut dire que le /health permet de vérifier aussi l'état de la base ded$ donnée.

Et éffectivement après avoir rallumé la BDD le /health renvoie bien un status `UP`

5 - Le méssage renvoyé quabnd la BDD est éteinte et que la l'application est relancé : 

Connection imposible a la base de donnée.
`HHH000247: ErrorCode: 0, SQLState: 08000
2026-11-24T15:05:11.696+01:00  WARN 26852 --- [compta-api] [           main] org.hibernate.orm.jdbc.error             : Socket fail to connect to localhost. Connection refused (connect failed)
2026-11-24T15:05:11.698+01:00  WARN 26852 --- [compta-api] [           main] org.hibernate.orm.jdbc                   : HHH100046: Could not obtain connection to query JDBC database metadata
`

6 - Commit

7 - Le lancement de l'application n'a pas marché car le schéma de la base de donée n'est pas correct, du au changement de dte_cmd a date_commande. 
Je vois aussi dans le terminal que ça me proposait de le générer.

C'est une bonne nouvelle en ce sens ou ça nous averti que le schéma de BDD n'est pas correcte, donc une possibilité de changement.
Par contre si c'étais un `upadate` a la place du validate ca aurais potentielement causé des dégats 😅

8 - Celui qui gagne est le fichier car c'est lui qui a été défini lors de la compilation.


---

#TP Devis injection routes

1 - Les test qui ont échoués : 

`
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.15.0:compile (default-compile) on project compta-api: Compilation failure: Compilation failure: 
[ERROR] /Users/hounkpe-sagbo/Downloads/tp-01-demarrer-compta-api/compta-api/src/main/java/org/sebsy/compta/controllers/DevisControllerTest.java:[3,29] package org.junit.jupiter.api does not exist
[ERROR] /Users/hounkpe-sagbo/Downloads/tp-01-demarrer-compta-api/compta-api/src/main/java/org/sebsy/compta/controllers/DevisControllerTest.java:[5,45] package org.springframework.boot.test.context does not exist
[ERROR] /Users/hounkpe-sagbo/Downloads/tp-01-demarrer-compta-api/compta-api/src/main/java/org/sebsy/compta/controllers/DevisControllerTest.java:[6,58] package org.springframework.boot.webmvc.test.autoconfigure does not exist
[ERROR] /Users/hounkpe-sagbo/Downloads/tp-01-demarrer-compta-api/compta-api/src/main/java/org/sebsy/compta/controllers/DevisControllerTest.java:[8,52] package org.springframework.test.web.servlet.assertj does not exist
[ERROR] /Users/hounkpe-sagbo/Downloads/tp-01-demarrer-compta-api/compta-api/src/main/java/org/sebsy/compta/controllers/DevisControllerTest.java:[10,35] package org.assertj.core.api does not exist
[ERROR] /Users/hounkpe-sagbo/Downloads/tp-01-demarrer-compta-api/compta-api/src/main/java/org/sebsy/compta/controllers/DevisControllerTest.java:[10,1] static import only from classes and interfaces
`

Les tests ont échouées parce que le controlleur n'étais pas dans le bon package et j'ai aussi relevé un probleme de dépendance

## Test de la premiere requeette : `curl "localhost:8081/api/devis?prixHt=2.25&quantite=10"`. La requette est erronée car le champ 'BigDecimal' n'est pas remplis

😹 Croorection la requette étais bonne c'est juste moi qui avais mis PrixHt au lieu de prixHt et voici le rendu : {"quantite":10,"ht":11.25,"ttc":12.94}

## Test de la deuxieme requette : `curl "localhost:8081/api/devis/3?prixHt=74.99"`. Le résultat {"quantite":3,"ht":224.97,"ttc":258.72}

## Test de la 3 eme requette : `curl "localhost:8081/api/devis?quantite=3"` . 


6 - `Reprendre cette requette :` Resulat : {"quantite":10,"ht":11.25,"ttc":13.50}

# TP 03 - Des repositories sur compta

5 - Prédiction : 

Resultats de show-sql : 

`
Hibernate: select a1_0.id,a1_0.actif,a1_0.designation,a1_0.id_fou,a1_0.prix,a1_0.ref from article a1_0 order by a1_0.ref
Hibernate: select f1_0.id,f1_0.rue,f1_0.code_postal,f1_0.ville,f1_0.nom from fournisseur f1_0 where f1_0.id=?
Hibernate: select f1_0.id,f1_0.rue,f1_0.code_postal,f1_0.ville,f1_0.nom from fournisseur f1_0 where f1_0.id=?
Hibernate: select f1_0.id,f1_0.rue,f1_0.code_postal,f1_0.ville,f1_0.nom from fournisseur f1_0 where f1_0.id=?
`



Explication des lignes :

- **Ligne 1** `select ... from article a1_0 order by a1_0.ref` : c'est la requête générée par `findAllByOrderByRefAsc()`, appelée dans `CatalogueService.tous()`. Elle charge tous les articles triés par référence. Elle ne lit que la colonne `id_fou` du fournisseur, pas le fournisseur lui-même, car `Article.fournisseur` est en `FetchType.LAZY` : Hibernate met à la place un proxy vide qui ne connaît que l'id.
- **Lignes 2 à 4** `select ... from fournisseur f1_0 where f1_0.id=?` : chacune charge **un** fournisseur. Elles sont déclenchées par `ArticleDto.of`, qui appelle `a.getFournisseur().getNom()`. Le premier accès à `getNom()` sur un proxy oblige Hibernate à aller chercher le fournisseur en base. Ça marche uniquement parce que la transaction (`@Transactional(readOnly = true)`) est encore ouverte.

Ce qui fixe leur nombre : **1 requête pour les articles + 1 requête par fournisseur distinct** présent dans les articles. Ma base a 12 articles répartis sur 3 fournisseurs (Française d'Imports, FDM SA, Dubois & Fils), d'où 1 + 3 = 4 requêtes, et non 1 + 12. Une fois qu'un fournisseur est chargé, il reste dans le contexte de persistance (le cache de premier niveau de la transaction) : les autres articles du même fournisseur le réutilisent sans nouvelle requête.

C'est le problème du **N+1** : si on ajoute des fournisseurs, le nombre de requêtes augmente avec eux. On pourrait tout charger en une seule requête avec un `JOIN FETCH` (`@Query`) ou un `@EntityGraph(attributePaths = "fournisseur")` sur la méthode du repository.
