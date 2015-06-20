(deftemplate bot
	(slot HP (default 15))
	(slot PP (default 15))
	(slot AP (default 5))
	(slot WP (default 0))
	(slot state (default current))
	(slot posX (default -1))
	(slot posY (default -1))
	(slot currentField (default normal))
	(slot time (default 0))
	(slot modifiedFlag (default false))
)
	
(deftemplate tile
	(slot x)
	(slot y)
	(slot type (default current))
	(slot fieldType (default NORMAL))
) 
	
(deftemplate helper
	(slot value)
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; HELPER RULES 

(defrule ifCannotUp
	(declare (salience -2))
	(bot (posY ?y) (state current))
	(test (<= ?y 0))
	=>
	(assert (helper (value cannotUp)))
)

(defrule ifCannotDown
	(declare (salience -2))
	(bot (posY ?y) (state current))
	(test (>= ?y 49))
	=>
	(assert (helper (value cannotDown)))
)

(defrule ifCannotLeft
	(declare (salience -2))
	(bot (posX ?x) (state current))
	(test (<= ?x 0))
	=>
	(assert (helper (value cannotLeft)))
)

(defrule ifCannotRight
	(declare (salience -2))
	(bot (posX ?x) (state current))
	(test (>= ?x 49))
	=>
	(assert (helper (value cannotRight)))
)

(defrule canUp
	(declare (salience -10))
	(helper (value wantUp))
	(not (helper (value cannotUp)))
	(bot (state current) (posX ?cpX) (posY ?cpY))
	(not
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX ?cpX) (eq ?pY (- ?cpY 1))))
			)
		)
	)
	=>
	(bind ?*slotUp* (+ ?*slotUp* 1))
)

(defrule canDown
	(declare (salience -10))
	(helper (value wantDown))
	(not (helper (value cannotDown)))
	(bot (state current) (posX ?cpX) (posY ?cpY))
	(not
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX ?cpX) (eq ?pY (+ ?cpY 1))))
			)
		)
	)
	=>
	(bind ?*slotDown* (+ ?*slotDown* 1))
)

(defrule canLeft
	(declare (salience -10))
	(helper (value wantLeft))
	(not (helper (value cannotLeft)))
	(bot (state current) (posX ?cpX) (posY ?cpY))
	(not
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX (- ?cpX 1)) (eq ?pY ?cpY)))
			)
		)
	)
	=>
	(bind ?*slotLeft* (+ ?*slotLeft* 1))
)

(defrule canRight
	(declare (salience -10))
	(helper (value wantRight))
	(not (helper (value cannotRight)))
	(bot (state current) (posX ?cpX) (posY ?cpY))
	(not
		(exists
			(and
				(bot (state past) (posX ?pX) (posY ?pY))
				(test (and (eq ?pX (+ ?cpX 1)) (eq ?pY ?cpY)))
			)
		)
	)
	=>
	(bind ?*slotRight* (+ ?*slotRight* 1))
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

(defrule search
	(declare (salience 0))
	?b <- (bot (state current))
	;(not (tile (fieldType WOOD)))
	;(not (tile (fieldType FOOD)))
	;(not (tile (fieldType CORPSE)))
	=>
	(assert (helper (value wantUp)))
	(assert (helper (value wantDown)))
	(assert (helper (value wantLeft)))
	(assert (helper (value wantRight)))
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;