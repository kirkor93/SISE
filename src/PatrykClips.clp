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

(defglobal ?*action* = STAY)
(defglobal ?*newX* = 0)
(defglobal ?*newY* = 0)

(defglobal ?*foodNeed* = 0)
(defglobal ?*fireNeed* = 0)
(defglobal ?*woodNeed* = 0)

(defglobal ?*FOOD_COST_AP* = 3)
(defglobal ?*FOOD_REGEN_HP* = 10)
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

(deffacts startup
	(NeedHPMult 10)
	(NeedPPMult 9)
	(NeedWPMult 5)
)

(deffunction GetDistance
	(?fX ?fY ?sX ?sY)
	
	(bind ?val (+ (abs (- ?fX ?sX)) (abs (- ?fY ?sY))))
	
	(printout t "GetDistance function" crlf)
	(return ?val)
	
)

(deffunction GetDirection
	(?dX ?dY ?bX ?bY)
		
	(printout t "GetDirection function" crlf)
	(if	(> (abs(- ?dY ?bY)) (abs(- ?dX ?bX)))
	then
		(if(> ?bY ?dY)
		then 
			(bind ?*newY* -1)
			(bind ?*newX* 0)
			(return)
		else
			(bind ?*newY* 1)
			(bind ?*newX* 0)
			(return)
		)
	else
		(if(> ?bX ?dX)
		then 
			(bind ?*newX* -1)
			(bind ?*newY* 0)
			(return)
		else
			(bind ?*newX* 1)
			(bind ?*newY* 0)
			(return)
		)	
	)
)


(defrule searchMap
	(declare (salience 10))
	(tileBase (fieldX ?fX) (fieldY ?fY) (fieldType ?fT))
	(bot (botX ?bX) (botY ?bY))
	?actionHand <- (actionHandler (enemiesCount ?eC) (foodCount ?fC) (corpseCount ?cC) (woodCount ?wC))
	=>
	(printout t "SearchMap rule" crlf)
	(switch ?fT
		(case ENEMY 
			then 
			(modify ?actionHand (enemiesCount (+ ?eC 1)))
			(assert (tileEnemy (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
		)
		(case WOOD 
			then 
			(modify ?actionHand (woodCount (+ ?wC 1)))
			(assert (tileWood (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
		)
		(case FOOD 
			then 
			(modify ?actionHand (foodCount (+ ?fC 1)))
			(assert (tileFood (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
		)
		(case CORPSE
			then 
			(modify ?actionHand (corpseCount (+ ?cC 1)))
			(assert (tileCorpse (fieldX  ?fX) (fieldY ?fY) (distance (GetDistance ?bX ?bY ?fX ?fY))))
		)
	)
)


(defrule prepareFood
	(declare (salience 9))
	?actionHand <- (actionHandler (distanceFood ?sD))
	(tileFood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	=>
	(printout t "PrepareFood rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceFood ?d))
	)
)

(defrule prepareCorpse
	(declare (salience 9))
	?actionHand <- (actionHandler (distanceCorpse ?sD))
	(tileCorpse (fieldX ?fX) (fieldY ?fY) (distance ?d))
	=>
	(printout t "PrepareCorpse rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceCorpse ?d))
	)
)


(defrule prepareWood
	(declare (salience 9))
	?actionHand <- (actionHandler (distanceWood ?sD))
	(tileFood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	=>
	(printout t "PrepareWood rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceWood ?d))
	)
)

(defrule prepareAttack
	(declare (salience 9))
	?actionHand <- (actionHandler (distanceEnemy ?sD))
	(tileEnemy (fieldX ?fX) (fieldY ?fY) (distance ?d))
	=>
	(printout t "PrepareAttack rule" crlf)
	(if (> ?sD ?d)
	then 
		(modify ?actionHand (distanceEnemy ?d))
	)
)

(defrule setNeeds
	(declare (salience 8))
	(bot (HP ?hp) (PP ?pp) (WP ?wp))
	(NeedHPMult ?nHPM)
	(NeedPPMult ?nPPM)
	(NeedWPMult ?nWPM)
	=>
	(printout t "SetNeeds rule" crlf)
	(bind ?*HPNeed* (* (- 15 ?hp) ?nHPM))
	(bind ?*PPNeed* (* (- 15 ?pp) ?nPPM))
	(bind ?*WPNeed* (* (- 15 ?wp) ?nWPM))
)
	
(defrule getMostImportantNeed
	(declare (salience 7))
	?act <- (actionHandler (action ?a))
	=>
	(printout t "MostImportantNeed rule" crlf)
	(if (and (> ?*HPNeed* 10) (> ?*HPNeed* ?*PPNeed*) (> ?*HPNeed* ?*WPNeed*))
	then
		(modify ?act (action FOOD))
	) 
	(if (and (> ?*PPNeed* 10) (> ?*PPNeed* ?*HPNeed*) (> ?*PPNeed* ?*WPNeed*))
	then
		(modify ?act (action FIRE))
	) 
	(if (and (> ?*WPNeed* 10) (> ?*WPNeed* ?*HPNeed*) (> ?*WPNeed* ?*PPNeed*))
	then
		(modify ?act (action WOOD))
	) 	
	(if (and (< ?*HPNeed* 10) (< ?*PPNeed* 10) (< ?*WPNeed* 10))
	then
		(modify ?act (action OTHER))
	)	
)

(defrule handleOTHER
	(declare (salience 6))
	(actionHandler (distanceEnemy ?sD) (enemiesCount ?eC))
	(bot (botX ?bX) (botY ?bY) (AP ?ap) (WP ?wp))
	(tileEnemy (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?act <- (actionHandler (action OTHER))
	=>
	(printout t "HandleOther rule" crlf)
	(if (> ?eC 0)
	then
		(if (and (= ?d ?sD) (> ?sD ?*THROW_MAX_DIST*))
		then
			(bind ?*action* MOVE)
			(GetDirection ?fX ?fY ?bX ?bY)
		else
			(if (> ?wp ?*THROW_COST_WP*)
			then
				(if (> ?ap ?*THROW_COST_AP*) 
				then
					(bind ?*action* THROW)
					(bind ?*newX* ?fX)
					(bind ?*newY* ?fY)
				else
					(bind ?*action* WAIT)
				)
			else
				(modify ?act (action WOOD))	
			)		
		)
	else
		(bind ?*action* MOVERAND)
	)
)

(defrule handleFIRE
	(declare (salience 5))
	(bot (botX ?bX) (botY ?bY) (AP ?ap) (WP ?wp))
	?act <- (actionHandler (action OTHER))
	=>
	(printout t "HandleFire rule" crlf)
	(if (> ?wp ?*FIRE_COST_WP*)
	then
		(if (> ?ap ?*FIRE_COST_AP*) 
		then
			(bind ?*action* KINDLE)
		else
			(bind ?*action* WAIT)			
		)
	else
		(modify ?act (action WOOD))
	)
)

(defrule handleWOOD
	(declare (salience 5))
	(actionHandler (distanceWood ?sD) (woodCount ?eC))
	(bot (botX ?bX) (botY ?bY) (AP ?ap))
	(tileWood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	?act <- (actionHandler (action WOOD))
	=>
	(printout t "HandleWood rule" crlf)
	(if(> ?eC 0)
	then
		(if (and (= ?d ?sD) (> ?sD 2))
		then
			(bind ?*action* MOVE)
			(GetDirection ?fX ?fY ?bX ?bY)
		else
			(if (> ?ap ?*WOOD_COST_AP*) 
			then
				(bind ?*action* MOVE)
				(bind ?*newX* ?fX)
				(bind ?*newY* ?fY)
			else
				(bind ?*action* WAIT)
			)		
		)
	else
		(bind ?*action* MOVERAND)
	)
)

(defrule handleFOOD
	(declare (salience 5))
	?act <- (actionHandler (action FOOD))
	(actionHandler (distanceFood ?sD) (foodCount ?eC))
	(bot (botX ?bX) (botY ?bY) (AP ?ap))
	(tileFood (fieldX ?fX) (fieldY ?fY) (distance ?d))
	=>
	(printout t "HandleFood rule" crlf)
	(if(> ?eC 0)
	then
		(if (and (= ?d ?sD) (> ?sD 2))
		then
			(bind ?*action* MOVE)
			(GetDirection ?fX ?fY ?bX ?bY)
		else
			(if (> ?ap ?*FOOD_COST_AP*) 
			then
				(bind ?*action* MOVE)
				(bind ?*newX* ?fX)
				(bind ?*newY* ?fY)
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
	?act <- (actionHandler (action FOOD))
	(actionHandler (distanceCorpse ?sD) (corpseCount ?eC) (foodCount ?fC))
	(bot (botX ?bX) (botY ?bY) (AP ?ap)(PP ?pp))
	(tileCorpse (fieldX ?fX) (fieldY ?fY) (distance ?d))
	=>
	(printout t "HandleCoprse rule" crlf)
	(if (and (< ?fC 1) (> ?pp 9))
	then 
		(if(> ?eC 0)
		then
			(if (and (= ?d ?sD) (> ?sD 2))
			then
				(bind ?*action* MOVE)
				(GetDirection ?fX ?fY ?bX ?bY)
			else
				(if (> ?ap ?*FOOD_COST_AP*) 
				then
					(bind ?*action* MOVE)
					(bind ?*newX* ?fX)
					(bind ?*newY* ?fY)
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
