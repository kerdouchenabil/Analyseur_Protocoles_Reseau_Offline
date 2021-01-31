Présentation du projet Network protocol analyser

Cet analyseur de protocoles réseau prend en entrée un fichier trace contenant les octets capturés sur un réseau Ethernet. Il s’affiche dans une interface graphique.
La liste des protocoles que l'analyseur est en mesure de traiter sont: Ethernet, IP, TCP, HTTP, les autres ne seront donc pas traités. L'utilisateur peut ensuite voir les résultats de l'analyse sur la fenêtre graphique et aussi le terminal. Il peut aussi la sauvegarder dans un fichier texte.
Le projet est implémenté en langage java, programmation orientée objets.


Structure du code du projet Network Protocol Analyser

Classe Analyseur:
Porte d’entrée du programme, qui contient donc la fonction main. Elle lance donc une fenêtre graphique implémentée avec javaFX. Contient la fonction analyser() qui s’occupera de coordonner le traitement de toutes les phases d’analyse, en commençant par faire appel à la classe Trace. Ensuite fait passer la trace dans un filtre (classe Filtre) pour avoir en sortie une liste de trames (classe Trame). Ces trames seront ensuite analysées par les différentes classes correspondant successivement aux couches réseau. Si une des couches détecte une erreur dans l’une des trames, elle l’a signale directement à la classe Analyseur qui s’occupera d’afficher cela et de passer à la trame suivante.


Classe Trace:
Ouvre un fichier et s’occupe d’en extraire une chaîne de caractères qui sera ensuite reconnue par le filtre. Elle signale une erreur de lecture si le format du fichier est invalide.


Classe Filtre : 
Génère une liste de trames à partir d’une trace. Selon leur ordre dans la trace sans offset pour faciliter ensuite le traitement pour les autres classes, les lignes vides sont ignorées, si l’offset n’est pas codé sur un octet ou plus on ignore la ligne, si l’offset ne correspond pas on ignore la ligne  et  tout élément qui n’est pas un octet est ignoré cela permet de récupérer uniquement les trames contenu dans la trace et rien d’autre.   


Classe Trame:
Contient une liste d’octets, sachant qu’un octet est représenté par deux caractères hexadécimaux.


Classe Ethernet:
Prend une Trame en paramètre, elle extrait tous les champs d’Ethernet et les sauvegarde dans des variables de classe qui seront donc affichées ensuite par la fonction toString(). Si une erreur est détectée tel qu’un nombre d’octets insuffisant, elle sera signalée directement à l’analyseur. Cette classe reconnaîtra donc le type du protocole encapsulé dans ses données. Si le protocole est IP, l’analyse sera poursuivie dans classe IP qui prendra justement les données restantes. Sinon, l’analyseur affiche juste le type du protocole sans l’analyser, et passe donc à la trame suivante.


Classe IP: 
IP est créée si le protocole  de la trame ethernet est bien IP alors dans ce cas la on récupère les octets correspondant à IP, un traitement est ensuite effectué pour récupérer les différents champs de IP et ses options. Quelques contrôles d’erreur sont effectués comme le checksum et  la longueur de l’entête et la longueur totale. Toute entête IP incomplète sera signalée à Analyser. Si le protocole est TCP, l’analyse sera poursuivie dans la classe TCP qui prendra les données restantes. Sinon, l’analyseur affiche juste le type du protocole sans l’analyser, et passe donc à la trame suivante.


Classe Options:
Cette classe s’occupe de traiter les options de TCP et IP. Elle prend en paramètre une liste d’octets que va lui passer IP ou TCP, pour en extraire les options une par une, elle les sauvegarde donc dans une liste d’options. Si une option est invalide (par exemple qui a un type non reconnu, ou une longueur supérieure à celle du champ options tout entier) elle sera ainsi signalée comme option invalide ou juste non reconnue selon le cas. La classe interne Option représente donc une option, selon son type et sa longueur elle sera traitée par une fonction spécifique pour extraire les différentes informations.


Classe TCP : 
TCP est créée si le champ protocole  de IP est bien TCP, alors dans ce cas là on récupère les octets correspondant à TCP, un traitement est ensuite effectué pour récupérer les différents champs de TCP et ses options.Toute entête TCP incomplète ou contenant une erreur sera signalée à Analyseur. Si l’un des port de TCP destination ou source est égale à 80 l’analyse sera poursuivi dans la classe HTTP qui prendra les données restantes.   


Classe HTTP : 
HTTP est créée si l’un des ports TCP est égal à 80 si c’est source qui est à 80 cela veut dire que c’est une réponse HTTP si c’est destination alors c’est une requette HTTP. Un traitement est ensuite effectué pour récupérer premièrement la ligne de requête ou de réponse, puis les différentes lignes d’entête qui suivent et qui consistent donc à une conversion d’octets en code ascii et de faire correspondre chaque ligne a son code ascii. Elles seront sauvegardées dans une liste de lignes de longueur variable (Classe interne Ligne).


Classe InvalidTrameException : 
Utilisé dans les différentes étapes de traitement des trames qui nous permet de gérer les erreurs et/ou de les signaler à Analyseur.  


Classe Convert
Un outil utilisé pour toute conversion, par exemple de l'hexadécimal en décimal ou en binaire et vice versa et de octet en code Ascii.

