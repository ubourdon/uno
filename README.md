functional event sourcing UNO
=============================

Démontrer le concept d'evensourcing en utilisant la programmation fonctionnelle en utilisant Scala.

http://thinkbeforecoding.github.io/FsUno.Prod/

http://fr.slideshare.net/ScottWlaschin/ddd-with-fsharptypesystemlondonndc2013

http://blog.pellucid.com/post/94532532890/scalas-modular-roots-by-dan-james-earlier-this

Thèmes du Kata
==============

Definir Decide: Command => State => List[Event] & Apply: State => Event => Event

Introduire FunctionalEventSourcingTestFramework

Introduire le jeu UNO qui illustrera le Kata
Lister les premiers évènements décrivant le Domaine du jeu UNO (EventStorming)

Commencer les premiers pas TDD en implémentant les premiers évènments 

Que doit-on mettre dans un évènement ? 
Les évènement doivent contenir le résultat de l'application d'une règle métier
ex: Joueur a joué une carte (+2) => faut-il avoir aussi un évènement joueur suivant prend deux cartes ?

Quand est-ce qu'une erreur doit être traitée à travers un évènement ou comme une erreur ?
ex: je joue pas à mon tour => évènement "joueur a pris 2 cartes"
joueur joue une carte qu'il n'a pas dans son jeu => erreur

