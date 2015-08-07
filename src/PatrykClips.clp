(deftemplate bot
	(slot HP (default 15) (type INTEGER))
	(slot PP (default 15) (type INTEGER))
	(slot AP (default 5) (type INTEGER))
	(slot WP (default 0) (type INTEGER))
	(slot botX (default -1) (type INTEGER))
	(slot botY (default -1) (type INTEGER))
	(slot currentField (default normal))
)

(deftemplate tile
	(slot fieldX (type INTEGER))
	(slot fieldY (type INTEGER))
	(slot type (default current))
	(slot fieldType (default NORMAL))
)

(defglobal ?*action* = stay)
(defglobal ?*newX* = 0)
(defglobal ?*newY* = 0)

(defglobal ?*foodNeed* = 0)
(defglobal ?*fireNeed* = 0)
(defglobal ?*woodNeed* = 0)
(defglobal ?*enemyNeed* = 0)
(defglobal ?*trapNeed* = 0)

(deffunction GetDistance
	(?fX ?fY ?sX ?sY)
	
	(bind ?val (+ (abs (- ?fX ?sX)) (abs (- ?fY ?sY))))
	
	(return ?val)
)

(deffunction GetDirection
	(?dX ?dY)
	(bot (botX ?bX) (botY ?bY))
		
	(if	( > (abs( -?dY ?bY) abs(- ?dX ?bX)))
		then
			(if( > (?bY ?dY))
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
			(if( > (?bX ?dX))
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
	(printout t "ERROR in SolveDirection function" crlf)
	(return)
)

(defrule setNeeds
	(bot (HP ?hp) (PP ?pp) (WP ?wp) (botX ?bX) (botY ?bY))
	(tile (state ?tState) (type ?t) (x ?dX) (y ?dY))
	=>
	
	
	
	
	