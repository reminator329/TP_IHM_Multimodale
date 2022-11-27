# Rapport IHM Multimodale

- Kahina Chalabi
- Rémi Laborie

[https://github.com/reminator329/TP_IHM_Multimodale](https://github.com/reminator329/TP_IHM_Multimodale)


# Introduction

Le but de ce projet est de spécifier, concevoir et implémenter un moteur de fusion multimodale pour interagir avec une application, avec la parole et le pointage (en utilisant la souris) afin de créer, déplacer ou supprimer des formes.

# Utilisation de l’application

Nous avons choisi trois action possibles qui sont : créer, déplacer et supprimer, quatre formes possibles : losange, triangle, rectangle et cercle
et trois couleurs : rouge, vert et bleu sinon gris par défaut.

# Chronogrammes

![https://raw.githubusercontent.com/reminator329/TP_IHM_Multimodale/master/images/Chronogrammes.png](https://raw.githubusercontent.com/reminator329/TP_IHM_Multimodale/master/images/Chronogrammes.png)

Parmi les chronogrammes que nous avons fait, certain ne sont pas pris en compte dans notre application : les cas 1, 3, 4, 6, 7 et 9. Car nous ne faisons pas d’action monomodale ; nous n’avons pas implémenté le fait de pouvoir créer une forme sans donner de localisation ; nous n’avons pas implémenté le choix de la couleur par clic ; nous n’avons pas pris en compte dans la grammaire le cas qui ne se dit pas. Pour le cas 9, l’action fonctionne mais ne prend pas en compte la forme ni la couleur dite par l’utilisateur, l’application déplacera une des formes se trouvant sous le clic.

Pour les cas 8 à 10, “forme” peut être le nom de la forme ou bien les formulations “cette forme” ou “ça” (correspondant au “where” de la grammaire).

Sur les chronogramme, on peut voir des crochets au niveau des clics correspondant au temps de capture d’un clic. Cependant, notre application de fonctionne pas comme cela. En réalité, les clics sont capturés indépendamment de la voix. Une fois que la voix est capturée, l’application regarde si l’utilisateur à fait suffisamment de clics depuis la dernière commande ou attend d’en recevoir avant la prochaine commande et exécutera l’action une fois toutes les informations récoltées.

# Diagramme de classes

![https://raw.githubusercontent.com/reminator329/TP_IHM_Multimodale/master/images/Diagramme_classes.png](https://raw.githubusercontent.com/reminator329/TP_IHM_Multimodale/master/images/Diagramme_classes.png)

Dans ce diagramme, nous pouvons voir la gestion des classes dans le code. La classe Main exécutant le code principal utilise une machine à état FSM expliquée dans la partie dédiée.
La classe Data permet de stocker toutes les informations nécessaires à la fusion des informations fournies par l’utilisateur pour exécuter l’action demandée.

# Machine à états

![Untitled](../images/Untitled.png)

Première étape à faire c’est de lancer sra5 avec la grammaire parole, pour récupérer la voix sur le bus Ivy.

Nous avons 4 états possible. Init est l’état d’initialisation (affichage de l’application) avec un clic de souris nous aurons accès à un tableau blanc et l’application commence à écouter les différents événements (voix et clics), ce qui nous mène à l’état pré-fusion.

Ici l’utilisateur parle et dit ce qu’il souhaite faire. L’application reste sur le tableau blanc tout en écoutant les événements. L’action parole permet de récupérer toutes les informations dites par l’utilisateur. Lorsque l’on reçoit une phrase, on passe à l’état possible-fusion.

Cet état permet de vérifier la cohérence des informations données par les différentes modalités. De plus, si les informations disponibles dans cet état ne sont pas suffisantes ou cohérentes, l’application continue d’enregistrer les nouvelles informations qui peuvent apparaître (les clics pour notre application par exemple). Concernant la voix, l’application enregistre une nouvelle action en supposant que la précédente est incohérente car trop longue. Une fois que les informations sont cohérentes on passe à l’état fusion.

Celui-ci permet de faire la commande demandée par l’utilisateur. Une fois l’action réalisée, on retourne à l’état de pré-fusion.

# Exemples d’utilisation

![Écran d’accueil de l’application](../images/Untitled%201.png)

Écran d’accueil de l’application

![Résultat après avoir dit “Créer un triangle rouge ici” et un clic](../images/Untitled%202.png)

Résultat après avoir dit “Créer un triangle rouge ici” et un clic

![Résultat après avoir dit “Déplace ce triangle rouge ici” et deux clic : un sur le triangle et un sur la destination](../images/Untitled%203.png)

Résultat après avoir dit “Déplace ce triangle rouge ici” et deux clic : un sur le triangle et un sur la destination

![Résultat après avoir dit “Supprime ce triangle” et un clic sur le triangle](../images/Untitled%204.png)

Résultat après avoir dit “Supprime ce triangle” et un clic sur le triangle

# Exemple vidéo

[https://www.youtube.com/watch?v=7Gak6B1yGxM](https://www.youtube.com/watch?v=7Gak6B1yGxM)