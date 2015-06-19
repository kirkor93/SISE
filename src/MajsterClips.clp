(deftemplate bot
	(slot x)
	(slot y)
	(slot hitPoints (default 15))
	(slot actionPoints (default 5))
	(slot woodPoints (default 0))
	(slot psychicPoints (default 15)))
	
(defglobal
	?*dir* = random
)

(defglobal
	?*iks* = 0
)

(defglobal
	?*igrek* = 0
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
			
(defrule kindleFire
	(bot (actionPoints ?ap) (woodPoints ?wp) (psychicPoints ?pp))
	(test (and (> ?ap 3) (>= ?wp 1) (< ?pp 0)))
	=>
	(bind ?*dir* kindle))
	
(defrule getCloseToEnemy
	(bot (hitPoints ?hp) (woodPoints ?wp))
	(tile (direction ?d) (distance ?l (type neighbour) (fieldType ENEMY))
	(test (and (> ?l 5) (> ?wp 1) (> ?hp 10)))
	=>
	(bind ?*dir* ?d))
	
(defrule attack
	(bot (hitPoints ?hp) (actionPoints ?ap) (woodPoints ?wp))
	(tile (x ?x) (y ?y) (distance ?l) (type neighbour) (fieldType ENEMY))
	(test (and (< ?l 5) (= ?ap 5) (>= ?wp 1) (> ?hp 10)))
	=>
	(bind ?*iks* ?x)
	(bind ?*igrek* ?y)
	(bind ?*dir* attack))
	
