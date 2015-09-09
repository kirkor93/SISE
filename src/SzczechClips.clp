(deftemplate player
	(slot HP (default 15) (type INTEGER))
	(slot PP (default 15) (type INTEGER))
	(slot AP (default 5) (type INTEGER))
	(slot WP (default 0) (type INTEGER))
	(slot X (default -1) (type INTEGER))
	(slot Y (default -1) (type INTEGER))
)

(deftemplate mapSlot
	(slot x (type INTEGER))
	(slot y (type INTEGER))
	(slot fieldType (default NORMAL))
	(slot distance (default 10000) (type INTEGER))
)

(deftemplate closestSlot
	(slot x (type INTEGER))
	(slot y (type INTEGER))
	(slot fieldType (default NORMAL))
	(slot distance (default 10000) (type INTEGER))
)

(defglobal ?*selectedAction* = Wait)
(defglobal ?*selectedX* = 0)
(defglobal ?*selectedY* = 0)

(defglobal ?*FoodMoveCostAP* = 3)
(defglobal ?*WoodMoveCostAP* = 3)
(defglobal ?*KindleFireCostAP* = 3)
(defglobal ?*KindleFireCostWP* = 1)
(defglobal ?*SetTrapCostAP* = 3)
(defglobal ?*SetTrapCostWP* = 1)
(defglobal ?*ThrowSpearCostAP* = 5)
(defglobal ?*ThrowSpearCostWP* = 1)
(defglobal ?*ThrowDistance* = 4)
(defglobal ?*EatCorpseCostAP* = 3)
(defglobal ?*EatCorpseCostPP* = 3)

(deffunction calculateDistance (?aX ?aY ?bX ?bY)
	
	(bind ?val (+ (abs (- ?aX ?bX)) (abs (- ?aY ?bY))))	
	(return ?val)	
)

(defrule processMap
	(declare (salience 12))
	?slot <- (mapSlot (x ?posX) (y ?posY) (fieldType ?fType))
	(player (X ?x) (Y ?y))
	=>
	(assert (mapSlot (x ?posX) (y ?posY) (fieldType ?fType) (distance (calculateDistance ?x ?y ?posX ?posY))))
)

(defrule getClosestWood
	(declare (salience 11))
	(mapSlot (fieldType WOOD) (x ?newPosX) (y ?newPosY) (distance ?newDist))
	?closest <- (closestSlot (fieldType WOOD) (x ?posX) (y ?posY) (distance ?dist))
	(and (test (< ?newDist ?dist))
		(test (> ?newDist 0)))
	=>
	;(printout t "Found closest wood: " ?newPosX " " ?newPosY " " ?newDist crlf)
	(modify ?closest (x ?newPosX) (y ?newPosY) (distance ?newDist))
)

(defrule getClosestFood
	(declare (salience 11))
	(mapSlot (fieldType FOOD) (x ?newPosX) (y ?newPosY) (distance ?newDist))
	?closest <- (closestSlot (fieldType FOOD) (x ?posX) (y ?posY) (distance ?dist))
	(and (test (< ?newDist ?dist))
		(test (> ?newDist 0)))
	=>
	;(printout t "Found closest food: " ?newPosX " " ?newPosY " " ?newDist crlf)
	(modify ?closest (x ?newPosX) (y ?newPosY) (distance ?newDist))
)

(defrule getClosestCorpse
	(declare (salience 11))
	(mapSlot (fieldType CORPSE) (x ?newPosX) (y ?newPosY) (distance ?newDist))
	?closest <- (closestSlot (fieldType CORPSE) (x ?posX) (y ?posY) (distance ?dist))
	(and (test (< ?newDist ?dist))
		(test (> ?newDist 0)))
	=>
	;(printout t "Found closest corpse: " ?newPosX " " ?newPosY " " ?newDist crlf)
	(modify ?closest (x ?newPosX) (y ?newPosY) (distance ?newDist))
)

(defrule getClosestEnemy
	(declare (salience 11))
	(mapSlot (fieldType ENEMY) (x ?newPosX) (y ?newPosY) (distance ?newDist))
	?closest <- (closestSlot (fieldType ENEMY) (x ?posX) (y ?posY) (distance ?dist))
	(and (test (< ?newDist ?dist))
		(test (> ?newDist 0)))
	=>
	;(printout t "Found closest enemy: " ?newPosX " " ?newPosY " " ?newDist crlf)
	(modify ?closest (x ?newPosX) (y ?newPosY) (distance ?newDist))
)

(defrule lifeInDanger 
	(declare (salience 12))
	(player (HP ?hp))
	(test (< ?hp 20))
	=>
	;(printout t "Life in danger rule, HP: " ?hp crlf)
	(assert (HPLow))
)

(defrule lifeInDanger2 
	(declare (salience 12))
	(player (PP ?pp))
	(test (< ?pp 20))
	=>
	;(printout t "Life in danger 2 rule, PP: " ?pp crlf)
	(assert (PPLow))
)

(defrule getMostImportantNeed
	(declare (salience 10))
	(player (HP ?hp)(PP ?pp))
	=>
	(if (<= ?hp ?pp)
	then
		(assert (mostImportantNeed Food))
	else
		(assert (mostImportantNeed KindleFire))
	)
)

(defrule checkShoot
	(declare (salience 10))
	(player (HP ?hp)(PP ?pp)(WP ?wp))
	=>
	(if (and (> ?hp 15)(> ?pp 15))
	then
		(assert (shootPossible))
	)	
)

(defrule tryPsychicalHeal
	(declare (salience 6))
	?need <- (mostImportantNeed KindleFire)
	(player (WP ?wood))
	=>
	;(printout t "tryPsychicalHeal" crlf)
	(if (>= ?wood ?*KindleFireCostWP*)
	then
		(bind ?*selectedAction* KindleFire)
		(retract ?need)
	)
)

(defrule tryCollectWood
	(declare (salience 5))
	(mostImportantNeed KindleFire)
	(closestSlot (fieldType WOOD) (x ?posX) (y ?posY) (distance ?dist))
	=>
	;(printout t "tryCollectWood" crlf)
	(bind ?*selectedAction* Move)
	(bind ?*selectedX* ?posX)
	(bind ?*selectedY* ?posY)
)

(defrule tryHeal
	(declare (salience 5))
	(mostImportantNeed Food)
	(closestSlot (fieldType FOOD) (x ?posX) (y ?posY) (distance ?dist))
	=>
	;(printout t "tryHeal" crlf)
	(bind ?*selectedAction* Move)
	(bind ?*selectedX* ?posX)
	(bind ?*selectedY* ?posY)
)

(defrule tryThrowSpear
	(declare (salience 4))
	(shootPossible)
	(closestSlot (fieldType ENEMY) (x ?posX) (y ?posY) (distance ?dist))
	=>
	;(printout t "tryCollectWood" crlf)
	(if (<= ?dist ?*ThrowDistance*)
	then
		(bind ?*selectedAction* ThrowSpear)
		(bind ?*selectedX* ?posX)
		(bind ?*selectedY* ?posY)
	)
)





















