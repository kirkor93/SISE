(deftemplate bot
	(slot x)
	(slot y)
	(slot hitPoints (default 15))
	(slot actionPoints (default 5))
	(slot woodPoints (default 0)))
	
(defglobal
	?*dir* = up
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
	
;(defrule goFood
;	(bot (hitPoints ?hp) (actionPoints ?ap))
;	(tile (direction ?d) (type neighbour) (fieldType ?t))
;	(test (and (>= ?ap 3) (eq ?t FOOD)))
;	=>
;	(printout t ?ap crlf)
;	(bind ?*dir* ?d))
		
(defrule detectFood
	(bot (x ?x) (y ?y) (actionPoints ?ap))
	(tile (direction ?d) (distance ?l) (type neighbour) (fieldType FOOD))
	=>
	(if (> ?ap 3)
		then
		(bind ?*dir* ?d)
		else
		(if (< ?ap 3)
			then
			(bind ?*dir* wait))))
			
(defrule detectWood
	(bot (actionPoints ?ap))
	(tile (direction ?d) (distance ?l) (type neighbour) (fieldType WOOD))
	=>
	(if (< ?ap 3)
	then
		(bind ?*dir* wait)))
	
;(defrule idleWalk
;	(bot (x ?x) (y ?y) (actionPoints ?ap))
;	(tile (distance ?l) (type neighbour) (fieldType NORMAL))
;	=>
;	(if (and (eq ?t NORMAL) (= ?l 4))
;		then
;		(bind ?*dir* up)))