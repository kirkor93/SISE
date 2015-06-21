(deftemplate bot
	(slot HP (default 15) (type INTEGER))
	(slot PP (default 15) (type INTEGER))
	(slot AP (default 5) (type INTEGER))
	(slot WP (default 0) (type INTEGER))
	(slot state (default current))
	(slot posX (default -1) (type INTEGER))
	(slot posY (default -1) (type INTEGER))
	(slot currentField (default normal))
	(slot time (default 0) (type INTEGER))
	(slot modifiedFlag (default false))
)
	
(deftemplate tile
	(slot x (type INTEGER))
	(slot y (type INTEGER))
	(slot type (default current))
	(slot fieldType (default NORMAL))
) 
	
(deftemplate helper
	(slot value)
	(slot numValue (type INTEGER) (default 0))
)

;INTERFACE
;NTH UP DWN LFT RGT FIR TRP THR
(defglobal ?*slotUp* = -1)
(defglobal ?*slotDown* = -1)
(defglobal ?*slotLeft* = -1)
(defglobal ?*slotRight* = -1)
(defglobal ?*slotFire* = -1)
(defglobal ?*slotTrap* = -1)
(defglobal ?*slotThrow* = -1)

(defglobal ?*throwCoordX* = 0)
(defglobal ?*throwCoordY* = 0)

;INTERNAL
(defglobal ?*MAX_BOT_TIME* = 5)

(defglobal ?*FOOD_COST_AP* = 3)
(defglobal ?*FOOD_REGEN_HP* = 10)
(defglobal ?*WOOD_COST_AP* = 3)
(defglobal ?*FIRE_COST_AP* = 3)
(defglobal ?*FIRE_COST_WP* = 1)
(defglobal ?*TRAP_COST_AP* = 3)
(defglobal ?*TRAP_COST_WP* = 1)
(defglobal ?*THROW_COST_AP* = 5)
(defglobal ?*THROW_COST_WP* = 1)
(defglobal ?*EAT_COST_AP* = 3)
(defglobal ?*EAT_COST_PP* = 3)
(defglobal ?*EAT_REGEN_HP* = 10)

(defglobal ?*PRIO_FOOD* = 50)
(defglobal ?*PRIO_WOOD* = 20)
(defglobal ?*PRIO_FIRE* = 12)
(defglobal ?*PRIO_FIGHT* = 5)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; FUNCTIONS

(deffunction SolveDirection
	(?cpX ?cpY ?pX ?pY ?val)
	
	(if	(and (> ?cpY ?pY) (<= ?cpX ?pX))
		then
		(bind ?*slotUp* (+ ?*slotUp* ?val))
		(return)
	)
	(if	(and (<= ?cpY ?pY) (< ?cpX ?pX))
		then
		(bind ?*slotRight* (+ ?*slotRight* ?val))
		(return)
	)
	(if	(and (< ?cpY ?pY) (>= ?cpX ?pX))
		then
		(bind ?*slotDown* (+ ?*slotDown* ?val))
		(return)
	)
	(if	(and (>= ?cpY ?pY) (> ?cpX ?pX))
		then
		(bind ?*slotLeft* (+ ?*slotLeft* ?val))
		(return)
	)
	(printout t "ERROR in SolveDirection function" crlf)
	(return)
)

(deffunction GetDistance
	(?cpX ?cpY ?pX ?pY)
	
	(bind ?val (+ (abs (- ?cpX ?pX)) (abs (- ?cpY ?pY))))
	
	(return ?val)
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; HELPER RULES 

(defrule ifCannotUp
	(declare (salience -2))
	(bot (posX ?x) (posY ?y) (state current) (AP ?ap))
	(tile (type neighbour) (y ?tpy) (fieldType ?ft))
	(or
		(test 
			(or
				(<= ?y 0)
				(and
					(< ?tpy ?y)
					(or
						(and
							(eq ?ft FOOD)
							(< ?ap ?*FOOD_COST_AP*)
						)
						(and
							(eq ?ft WOOD)
							(< ?ap ?*WOOD_COST_AP*)
						)
					)
				)
			)
		)
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX ?x) (eq ?pY (- ?y 1))))
			)
		)
	)
	=>
	(bind ?*slotUp*  -100)
)

(defrule ifCannotDown
	(declare (salience -2))
	(bot (posX ?x) (posY ?y) (state current) (AP ?ap))
	(tile (type neighbour) (y ?tpy) (fieldType ?ft))
	(or
		(test 
			(or
				(>= ?y 49)
				(and
					(> ?tpy ?y)
					(or
						(and
							(eq ?ft FOOD)
							(< ?ap ?*FOOD_COST_AP*)
						)
						(and
							(eq ?ft WOOD)
							(< ?ap ?*WOOD_COST_AP*)
						)
					)
				)
			)
		)
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX ?x) (eq ?pY (+ ?y 1))))
			)
		)
	)
	=>
	(bind ?*slotDown*  -100)
)

(defrule ifCannotLeft
	(declare (salience -2))
	(bot (posX ?x) (posY ?y) (state current) (AP ?ap))
	(tile (type neighbour) (x ?tpx) (fieldType ?ft))
	(or
		(test 
			(or
				(<= ?x 0)
				(and
					(< ?tpx ?x)
					(or
						(and
							(eq ?ft FOOD)
							(< ?ap ?*FOOD_COST_AP*)
						)
						(and
							(eq ?ft WOOD)
							(< ?ap ?*WOOD_COST_AP*)
						)
					)
				)
			)
		)
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX (- ?x 1)) (eq ?pY ?y)))
			)
		)
	)
	=>
	(bind ?*slotLeft*  -100)
)

(defrule ifCannotRight
	(declare (salience -2))
	(bot (posX ?x) (posY ?y) (state current) (AP ?ap))
	(tile (type neighbour) (x ?tpx) (fieldType ?ft))
	(or
		(test 
			(or
				(>= ?x 49)
				(and
					(> ?tpx ?x)
					(or
						(and
							(eq ?ft FOOD)
							(< ?ap ?*FOOD_COST_AP*)
						)
						(and
							(eq ?ft WOOD)
							(< ?ap ?*WOOD_COST_AP*)
						)
					)
				)
			)
		)
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX (+ ?x 1)) (eq ?pY ?y)))
			)
		)
	)
	=>
	(bind ?*slotRight*  -100)
)

(defrule forgetOldBots
	(declare (salience -20))
	?b <- (bot (state past) (time ?t))
	(test (> ?t ?*MAX_BOT_TIME*))
	=>
	(retract ?b)
)

;(defrule haveToWait
	;(declare (salience -5))
	;(not (helper (value canRight)))
	;(not (helper (value canLeft)))
	;(not (helper (value canUp)))
	;(not (helper (value canDown)))
	;=>
	;(bind ?*slotUp* (- ?*slotUp* 100))
	;(bind ?*slotDown* (- ?*slotDown* 100))
	;(bind ?*slotLeft* (- ?*slotLeft* 100))
	;(bind ?*slotRight* (- ?*slotRight* 100))
;)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; REAL DEAL

(defrule goFood
	(declare (salience 10))
	(bot (state current) (posX ?cpX) (posY ?cpY) (HP ?chp) (AP ?cap))
	(tile (fieldType FOOD) (type ?t) (x ?pX) (y ?pY))
	(test 
		(or 
			(not (eq ?t neighbour))
			(>= ?cap ?*FOOD_COST_AP*)
		)
	)
	?cl <- (helper (value closestFood) (numValue ?closest))
	(test (not (<= (GetDistance ?cpX ?cpY ?pX ?pY) 0)))
	(test (< (GetDistance ?cpX ?cpY ?pX ?pY) ?closest))
	=>
	(SolveDirection ?cpX ?cpY ?pX ?pY (round(/ ?*PRIO_FOOD* (GetDistance ?cpX ?cpY ?pX ?pY))))
	(modify ?cl (numValue (GetDistance ?cpX ?cpY ?pX ?pY)))
)


(defrule search
	(declare (salience 0))
	?b <- (bot (state current))
	=>
	(bind ?*slotUp*  (+ ?*slotUp* 1))
	(bind ?*slotDown*  (+ ?*slotDown* 1))
	(bind ?*slotLeft*  (+ ?*slotLeft* 1))
	(bind ?*slotRight*  (+ ?*slotRight* 1))
)