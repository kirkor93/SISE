(deftemplate bot
	(slot HP (default 15) (type INTEGER))
	(slot PP (default 15) (type INTEGER))
	(slot AP (default 5) (type INTEGER))
	(slot WP (default 0) (type INTEGER))
	(slot botX (default -1) (type INTEGER))
	(slot botY (default -1) (type INTEGER))
	(slot currentField (default NORMAL))
)

(deftemplate tileBase
	(slot fieldX (type INTEGER))
	(slot fieldY (type INTEGER))
	(slot fieldType (default NORMAL))
)

(deftemplate actionHandler
	(slot action (default WAIT))
	(slot distanceCorpse (type INTEGER) (default 1000))
	(slot distanceFood (type INTEGER) (default 1000))
	(slot distanceWood (type INTEGER) (default 1000))
	(slot distanceEnemy (type INTEGER) (default 1000))
	(slot foodCount (type INTEGER) (default 0))
	(slot corpseCount (type INTEGER) (default 0))
	(slot woodCount (type INTEGER) (default 0))
	(slot enemiesCount (type INTEGER) (default 0))
)

(deftemplate tileFood
	(slot fieldX (type INTEGER))
	(slot fieldY (type INTEGER))
	(slot distance (type INTEGER))
)

(deftemplate tileWood
	(slot fieldX (type INTEGER))
	(slot fieldY (type INTEGER))
	(slot distance (type INTEGER))
)

(deftemplate tileCorpse
	(slot fieldX (type INTEGER))
	(slot fieldY (type INTEGER))
	(slot distance (type INTEGER))
)

(deftemplate tileEnemy
	(slot fieldX (type INTEGER))
	(slot fieldY (type INTEGER))
	(slot distance (type INTEGER))
)

(defglobal ?*action* = WAIT)
(defglobal ?*newX* = 0)
(defglobal ?*newY* = 0)

(defglobal ?*foodNeed* = 0)
(defglobal ?*fireNeed* = 0)
(defglobal ?*woodNeed* = 0)

(defglobal ?*FOOD_COST_AP* = 3)
(defglobal ?*WOOD_COST_AP* = 3)
(defglobal ?*FIRE_COST_AP* = 3)
(defglobal ?*FIRE_COST_WP* = 1)
(defglobal ?*TRAP_COST_AP* = 3)
(defglobal ?*TRAP_COST_WP* = 1)
(defglobal ?*THROW_COST_AP* = 5)
(defglobal ?*THROW_COST_WP* = 1)
(defglobal ?*THROW_MAX_DIST* = 4)
(defglobal ?*EAT_COST_AP* = 3)
(defglobal ?*EAT_COST_PP* = 3)

(defglobal ?*HPNeed* = 0)
(defglobal ?*PPNeed* = 0)
(defglobal ?*WPNeed* = 0)

(defglobal ?*NeedHPMult* = 10)
(defglobal ?*NeedPPMult* = 9)
(defglobal ?*NeedWPMult* = 5)

(deffunction GetDistance
	(?fX ?fY ?sX ?sY)
	
	(bind ?val (+ (abs (- ?fX ?sX)) (abs (- ?fY ?sY))))
	
	;(printout t "GetDistance function" crlf)
	(return ?val)
	
)

(deffunction GetDirection
	(?dX ?dY ?bX ?bY)
		
	;(printout t "GetDirection function" crlf)
	
	(if (> (abs (- ?bY ?dY)) (abs(- ?bX ?dX)))
	then
		(if (> ?bY ?dY)
		then
			(bind ?*newX* 0)
			(bind ?*newY* -1)
			(return)
		else
			(if (< ?bY ?dY)
			then
				(bind ?*newX* 0)
				(bind ?*newY* 1)
				(return)
			)
		) 
	else
		(if (> ?bX ?dX)
		then
			(bind ?*newX* -1)
			(bind ?*newY* 0)
			(return)
		else
			(if (< ?bX ?dX)
			then
				(bind ?*newX* 1)
				(bind ?*newY* 0)
				(return)
			)
		) 
	)
	(bind ?*newX* 0)
	(bind ?*newY* 0)	
)


(defrule searchMap
	(declare (salience 20))
	(bot (botX ?bX) (botY ?bY))
	?tileB <-(tileBase (fieldX ?fX) (fieldY ?fY) (fieldType ?fT))
	?actionHand <- (actionHandler (enemiesCount ?eC) (foodCount ?fC) (corpseCount ?cC) (woodCount ?wC))
	=>
	;(printout t "SearchMap rule" crlf)
	(switch ?fT
		(case ENEMY 
			then 
			(if (and (!= ?bX ?fX) (!= ?bY ?fY))
			then
				;(printout t "SearchMap rule Enemy" crlf)
				(modify ?actionHand (enemiesCount (+ ?eC 1)))
				(assert (tileEnemy (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
				(assert (tileE (GetDistance ?bX ?bY ?fX ?fY)))
				(retract ?tileB)
			)
		)
		(case WOOD 
			then 
			;(printout t "SearchMap rule Wood" crlf)
			(modify ?actionHand (woodCount (+ ?wC 1)))
			(assert (tileWood (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
			(assert (tileW (GetDistance ?bX ?bY ?fX ?fY)))
			(retract ?tileB)
		)
		(case FOOD 
			then 
			;(printout t "SearchMap rule Food" crlf)
			(modify ?actionHand (foodCount (+ ?fC 1)))
			(assert (tileFood (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
			(assert (tileF (GetDistance ?bX ?bY ?fX ?fY)))
			(retract ?tileB)
		)
		(case CORPSE
			then 
			;(printout t "SearchMap rule Corpse" crlf)
			(modify ?actionHand (corpseCount (+ ?cC 1)))
			(assert (tileCorpse (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
			(assert (tileC (GetDistance ?bX ?bY ?fX ?fY)))
			(retract ?tileB)
		)
		(default
			(retract ?tileB)
		)
	)
)

(defrule prepareFood
	(declare (salience 9))
	(tileFood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?actionHand <- (actionHandler (distanceFood ?sD))
	?tF <- (tileF ?fD)
	(test (= ?d ?fD))
	=>
	;(printout t "PrepareFood rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceFood ?d))
	)
	(retract ?tF)
)

(defrule prepareCorpse
	(declare (salience 9))
	(tileCorpse (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?actionHand <- (actionHandler (distanceCorpse ?sD))
	?tC <- (tileC ?fC)
	(test (= ?d ?fC))
	=>
	;(printout t "PrepareCorpse rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceCorpse ?d))
	)
	(retract ?tC)
)


(defrule prepareWood
	(declare (salience 9))
	(tileWood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?actionHand <- (actionHandler (distanceWood ?sD))
	?tW <- (tileW ?fW)
	(test (= ?d ?fW))
	=>
	;(printout t "PrepareWood rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceWood ?d))
	)
	(retract ?tW)
)

(defrule prepareAttack
	(declare (salience 9))
	(tileEnemy (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?actionHand <- (actionHandler (distanceEnemy ?sD))
	?tE <- (tileE ?fE)
	(test (= ?d ?fE))
	=>
	;(printout t "PrepareAttack rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceEnemy ?d))
	)
	(retract ?tE)
)

(defrule setNeeds
	(declare (salience 8))
	(bot (HP ?hp) (PP ?pp) (WP ?wp))
	=>
	;(printout t "SetNeeds rule" crlf)
	(assert (NeedFlag))
	(bind ?*HPNeed* (* (- 15 ?hp) ?*NeedHPMult*))
	(bind ?*PPNeed* (* (- 15 ?pp) ?*NeedPPMult*))
	(bind ?*WPNeed* (* (- 15 ?wp) ?*NeedWPMult*))
)
	
(defrule getMostImportantNeed
	(declare (salience 7))
	?nF <- (NeedFlag)
	?act <- (actionHandler (action ?a))
	=>
	;(printout t "MostImportantNeed rule" crlf)
	(if (and (> ?*HPNeed* 60) (> ?*HPNeed* ?*PPNeed*) (> ?*HPNeed* ?*WPNeed*))
	then
		(modify ?act (action FOOD))
		(assert (NeedFood))
		;(printout t "FOOD" crlf)
	) 
	(if (and (> ?*PPNeed* 50) (> ?*PPNeed* ?*HPNeed*) (> ?*PPNeed* ?*WPNeed*))
	then
		(modify ?act (action FIRE))
		(assert (NeedFire))
		;(printout t "FIRE" crlf)
	) 
	(if (and (> ?*WPNeed* 30) (> ?*WPNeed* ?*HPNeed*) (> ?*WPNeed* ?*PPNeed*))
	then
		(modify ?act (action WOOD))
		(assert (NeedWood))
		;(printout t "WOOD" crlf)
	) 	
	(if (and (< ?*HPNeed* 30) (< ?*PPNeed* 30) (< ?*WPNeed* 20))
	then
		(modify ?act (action OTHER))
		(assert (NeedOther))		
		;(printout t "OTHERD" crlf)
	)
	(retract ?nF)	
)

(defrule handleOTHER
	(declare (salience 6))
	(bot (botX ?bX) (botY ?bY) (AP ?ap) (WP ?wp))
	(tileEnemy (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?nO <- (NeedOther)
	?act <- (actionHandler (distanceEnemy ?sD) (enemiesCount ?eC) (foodCount ?fC) (corpseCount ?cC) (woodCount ?wC))
	(test (= ?d ?sD))
	=>
	;(printout t "HandleOther rule" crlf)
	(retract ?nO)
	(if (> ?eC 0)
	then
		(if (and (= ?d ?sD) (> ?sD ?*THROW_MAX_DIST*))
		then
			(bind ?*action* MOVE)
			(GetDirection ?fX ?fY ?bX ?bY)
		else
			(if (>= ?wp ?*THROW_COST_WP*)
			then
				(if (>= ?ap ?*THROW_COST_AP*) 
				then
					(bind ?*action* THROW)
					(bind ?*newX* ?fX)
					(bind ?*newY* ?fY)
				else
					(bind ?*action* WAIT)
				)
			else
				;(modify ?act (action WOOD))	
				(assert (NeedWood))
			)		
		)
	else
		(if (and (> ?*HPNeed* ?*PPNeed*) (> ?*HPNeed* ?*WPNeed*))
		then
			;(modify ?act (action FOOD))	
			(assert (NeedFood))
		)
		(if (and (> ?*PPNeed* ?*HPNeed*) (> ?*PPNeed* ?*WPNeed*))
		then
			;(modify ?act (action FIRE))	
			(assert (NeedFire))
		)
		(if (and (> ?*WPNeed* ?*HPNeed*) (> ?*WPNeed* ?*PPNeed*))
		then
			;(modify ?act (action WOOD))			
			(assert (NeedWood))
		)
	)
)

(defrule handleFIRE
	(declare (salience 5))
	(bot (botX ?bX) (botY ?bY) (AP ?ap) (WP ?wp))
	?nF <- (NeedFire)
	;?act <- (actionHandler (action FIRE))
	=>
	(retract ?nF)
	;(printout t "HandleFire rule" crlf)
	(if (>= ?wp ?*FIRE_COST_WP*)
	then
		(if (>= ?ap ?*FIRE_COST_AP*) 
		then
			(bind ?*action* KINDLE)
		else
			(bind ?*action* WAIT)			
		)
	else
		(assert (NeedWood))
		;(modify ?act (action WOOD))
	)
)

(defrule handleWOOD
	(declare (salience 5))
	(actionHandler (distanceWood ?sD) (woodCount ?eC))
	(bot (botX ?bX) (botY ?bY) (AP ?ap))
	(tileWood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?nW <- (NeedWood)
	;?act <- (actionHandler (action WOOD))
	(test (= ?d ?sD))
	=>
	;(printout t "HandleWood rule" crlf)
	(if(> ?eC 0)
	then
		(if (> ?sD 1)
		then
			(bind ?*action* MOVE)
			(GetDirection ?fX ?fY ?bX ?bY)
		else
			(if (>= ?ap ?*WOOD_COST_AP*) 
			then
				(bind ?*action* MOVE)
				(GetDirection ?fX ?fY ?bX ?bY)
			else
				(bind ?*action* WAIT)
			)		
		)
	else
		(bind ?*action* MOVERAND)
	)
	(retract ?nW)
)

(defrule handleFOOD
	(declare (salience 5))
	(actionHandler (distanceFood ?sD) (foodCount ?eC))
	(bot (botX ?bX) (botY ?bY) (AP ?ap))
	(tileFood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?nF <- (NeedFood)
	;?act <- (actionHandler (action FOOD))
	(test (= ?d ?sD))
	=>
	;(printout t "HandleFood rule" crlf)
	(if(> ?eC 0)
	then
		(retract ?nF)
		(if (> ?sD 1)
		then
			(bind ?*action* MOVE)
			(GetDirection ?fX ?fY ?bX ?bY)
		else
			(if (>= ?ap ?*FOOD_COST_AP*) 
			then
				(bind ?*action* MOVE)
				(GetDirection ?fX ?fY ?bX ?bY)
			else
				(bind ?*action* WAIT)
			)		
		)
	else
		(bind ?*action* MOVERAND)
	)
)

(defrule handleCORPSE
	(declare (salience 4))
	(actionHandler (distanceCorpse ?sD) (corpseCount ?eC) (foodCount ?fC))
	(bot (botX ?bX) (botY ?bY) (AP ?ap)(PP ?pp))
	(tileCorpse (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?nF <- (NeedFood)
	;?act <- (actionHandler (action FOOD))
	(test (= ?d ?sD))
	=>
	;(printout t "HandleCoprse rule" crlf)
	(retract ?nF)
	(if (and (< ?fC 1) (> ?pp 9))
	then 
		(if (and (> ?eC 0) (> ?pp 5))
		then
			(if (> ?sD 1)
			then
				(bind ?*action* MOVE)
				(GetDirection ?fX ?fY ?bX ?bY)
			else
				(if (>= ?ap ?*FOOD_COST_AP*) 
				then
					(bind ?*action* MOVE)
					(GetDirection ?fX ?fY ?bX ?bY)
				else
					(bind ?*action* WAIT)
				)		
			)
		else
			(bind ?*action* MOVERAND)
		)
	else
		(bind ?*action* MOVERAND)
	)
)


