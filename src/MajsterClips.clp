(deftemplate bot
	(slot hitPoints (default 15))
	(slot actionPoints (default 5))
	(slot woodPoints (default 0))
	(slot state (default initial)))
	
(deffacts startup
	(bot))