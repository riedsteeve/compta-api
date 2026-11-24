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