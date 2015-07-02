(deftemplate bot
	(slot x (type INTEGER))
	(slot y (type INTEGER))
	(slot HP (default 15))
	(slot PP (default 15))
	(slot AP (default 5))
	(slot WP (default 0))
)

(deftemplate tile
	(slot fx (type INTEGER))
	(slot fy (type INTEGER))
	(slot type (default current))
	(slot fieldType (default NORMAL))
) 

(defglobal ?*action* = random)
(defglobal ?*distanceX* = 0)
(defglobal ?*distanceY* = 0)
(defglobal ?*enemyX* = 0)
(defglobal ?*enemyY* = 0)
(defglobal ?*psychic* = 5)

(deffunction getDistanceX
	(?x ?fx)
	(bind ?*distanceX* (- ?fx ?x))
	(return ?*distanceX*)
)

(deffunction getDistanceY
	(?y ?fy)
	(bind ?*distanceY* (- ?fy ?y))
	(return ?*distanceY*)
)

(defrule attack
	(bot (x ?x) (y ?y) (AP ?ap) (WP ?wp) (PP ?pp))
	(tile (fx ?fx) (fy ?fy) (type neighbour) (fieldType ENEMY))
	(test (and (= ?ap 5) (>= ?wp 2) (>= ?pp 8) (!= ?x ?fx) (!= ?y ?fy)))
	=>
	(bind ?*enemyX* ?fx)
	(bind ?*enemyY* ?fy)
	(bind ?*action* attack)
)
	
(defrule kindleFire
	(bot (AP ?ap) (WP ?wp))
	(test (and (>= ?ap 3) (>= ?wp 1)))
	=>
	(bind ?*action* kindle)
)

(defrule detectFood
	(bot (x ?x) (y ?y) (AP ?ap) (HP ?hp) (PP ?pp))
	(tile (fx ?fx) (fy ?fy) (type neighbour) (fieldType FOOD))
	(test (<= ?hp ?pp))
	=>
	(getDistanceX ?x ?fx)
	(getDistanceY ?y ?fy)
	(if (and (>= ?ap 3) (>= ?*distanceX* 1))
		then
		(bind ?*action* right))
	else
	(if (and (>= ?ap 3) (<= ?*distanceX* -1))
		then
		(bind ?*action* left))
	else
	(if (and (>= ?ap 3) (>= ?*distanceY* 1))
		then
		(bind ?*action* down))
	else
	(if (and (>= ?ap 3) (<= ?*distanceY* -1))
		then
		(bind ?*action* up))
	else
	(if (or (and (< ?ap 3) (>= ?*distanceX* -1) (<= ?*distanceX* 1) (= ?*distanceY* 0))
			(and (< ?ap 3) (= ?*distanceX* 0) (<= ?*distanceY* 1) (>= ?*distanceY* -1)))
		then
		(bind ?*action* wait))
)

(defrule detectWood
	(bot (x ?x) (y ?y) (HP ?hp) (PP ?pp) (AP ?ap))
	(tile (fx ?fx) (fy ?fy) (type neighbour) (fieldType WOOD))
	(test (<= ?pp ?hp))
	=>
	(getDistanceX ?x ?fx)
	(getDistanceY ?y ?fy)
	(if (and (>= ?ap 3) (>= ?*distanceX* 1))
		then
		(bind ?*action* right))
	(if (and (>= ?ap 3) (<= ?*distanceX* -1))
			then
			(bind ?*action* left))
	(if (and (>= ?ap 3) (>= ?*distanceY* 1))
		then
		(bind ?*action* down))
	(if (and (>= ?ap 3) (<= ?*distanceY* -1))
		then
		(bind ?*action* up))
	(if (and (< ?ap 3) (>= ?*distanceX* -1) (<= ?*distanceX* 1) 
						(>= ?*distanceY* 0) (<= ?*distanceY* 0))
		then
		(bind ?*action* wait))
)
