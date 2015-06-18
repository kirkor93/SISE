(deftemplate bot
	(slot HP (default 15))
	(slot PP (default 15))
	(slot WP (default 0))
	(slot state (default current))
	(slot posX (default -1))
	(slot posY (default -1))
	(slot currentField (default normal)))
	
(deftemplate tile
	(slot x)
	(slot y)
	(slot type (default current))
	(slot fieldType (default normal))) 
	