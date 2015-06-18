(deftemplate bot
	(slot x)
	(slot y)
	(slot hitPoints (default 15))
	(slot actionPoints (default 5))
	(slot woodPoints (default 0)))
	
(defglobal
	?*dir* = random
)
	
(deftemplate tile
	(slot x)
	(slot y)
	(slot direction)
	(slot distance (default 0))
	(slot type (default current))
	(slot fieldType (default NORMAL))) 
	
(deffacts startup
	(bot)
	(tile))
		
;(defrule idleWalk
;	(bot (x ?x) (y ?y) (actionPoints ?ap))
;	(tile (direction ?d) (distance ?l) (type neighbour) (fieldType NORMAL))
;	(not (any-factp ((?tile tile)) (not (?tile:fieldType NORMAL))))
;	=>
;	(bind ?*dir* random))
			
(defrule detectFood
	(bot (x ?x) (y ?y) (actionPoints ?ap))
	(tile (direction ?d) (distance ?l) (type neighbour) (fieldType FOOD))
	=>
	(if (>= ?ap 3)
		then
		(bind ?*dir* ?d)
		else
		(if (< ?ap 3)
			then
			(bind ?*dir* wait))))
			
(defrule detectWood
	(bot (actionPoints ?ap) (woodPoints ?wp))
	(tile (direction ?d) (distance ?l) (type neighbour) (fieldType WOOD))
	=>
	(if (>= ?ap 3)
		then
		(if (< ?wp 4)
		then 
			(bind ?*dir* ?d))
		else
		(if (< ?ap 3)
			then
			(bind ?*dir* wait))))