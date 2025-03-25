**Assegnamento 1 Sviluppo di un sistema client-server basato su socket e comunicazione 
multicast**
_traccia dell'assegnamento_
L'obiettivo è costruire un sistema distribuito in Java che simulazione il multicast tra un set di  nodi di comunicazione. 
In questo sistema, ogni nodo può inviare e ricevere messaggi che contengono l’ID del nodo e l’ID dei messaggi che mantiene il numero di messaggi ricevuti.  
In particolare, ogni volta che viene inviato un messaggio l’ID del messaggio viene incrementato di 1, ma se il messaggio è un messaggio reinviato allora l’ID non viene incrementato. 

Un messaggio è semplicemente definito dall'ID del nodo mittente e dall'ID dei messaggi (un 
numero intero). 
In particolare, ogni volta che viene inviato un messaggio l’ID viene  incrementato di 1, ma se il messaggio è un messaggio reinviato allora l’ID non è incrementato. 
Ogni nodo del sistema invia  messaggi multicast a tutti gli altri nodi, ma non tutti i messaggi  arrivano a destinazione. Infatti, l’invio di un messaggio può fallire perché può arrivare alla 
destinazione con una probabilità LP. 

Inoltre, quando un nodo del sistema  riceve un messaggio  con un ID del messaggio maggiore di quello previsto, allora questo nodo invia un messaggio in  multicast che indica la perdita di un messaggio aspettato; questo nuovo messaggio contiene 
l'ID del messaggio perso e l’ID del nodo che ha inviato il messaggio perso. Ricevuto il  messaggio, il nodo invia il messaggio atteso in multicast a tutti gli altri nodi. 

Il sistema contiene un server e ogni nodo conosce le informazioni per contattare il server. Il server ha il compito di chiedere ai nodi di iniziare lo scambio di messaggi, i nodi hanno il 
compito di informare il server sul completamento delle loro attività che viene raggiunto dai nodi 
quando l’ID  dei messaggi è uguale a cento. 

Quando il server ha ricevuto un messaggio di completamento da parte di tutti i nodi, allore il server chiede il sistema.

_esecuzione del codice_
1) Eseguire run del codice 'ObjectServer.java'
2) Eseguire 3 volte il codice 'ObjectClient.java'


