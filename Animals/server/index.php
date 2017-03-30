<?php
	define("USER", "id1151849_root");
	define("PASSWORD", "vfhbegjkm");
	define("HOST", "localhost");
	define("DB", "id1151849_animals");
	
	if(array_key_exists("test", $_GET))
	{
		print "Test OK.";
		exit;
	}
	if(array_key_exists("next", $_GET)) 
	{
		print getNext();
		//print $_GET["next"];
		exit;
	}
	if(array_key_exists("new", $_POST))
	{
		$json = $_POST["new"];
		print newOne($json);
		exit;
	}
	die("Unknown or empty key value.");
		
	function newOne($json)
	{
		$mysqli = new mysqli(HOST, USER, PASSWORD, DB);
		if($mysqli->connect_error)
			die("Error while connect database! ". $mysqli->connect_error);
		
		$arr = json_decode($json, true);
		$sql = sprintf("CALL CREATE_NEW(\"%s\", \"%s\", %d, %d)", $arr['question'], $arr['answear'], $arr['current'], $arr['yes']);
			
		if(!$mysqli->query($sql)) 
			die("Error while create newOne (".$mysqli->errno.") ".$mysqli->error);
		
		$mysqli->close();
		return "OK";
	}

	function getNext()
	{
		$id = (int) htmlspecialchars($_GET["next"]);
		if(empty($id)) die("Error: Empty GET next value");

		$mysqli = new mysqli(HOST, USER, PASSWORD, DB);
		if($mysqli->connect_error)
			die("Error while connect database! ". $mysqli->connect_error);

		$sql = sprintf("SELECT NODE_ID, QUESTION, ANSWEAR, YES_NODE_ID, NO_NODE_ID FROM nodes WHERE NODE_ID = %d", $id);
		
		$res = $mysqli->query($sql);
		if($res->num_rows === 0)
		{
			$json = "Empty row with id = ".$id;
		} else {		
			$row = $res->fetch_row();
			$json = array(
						'node_id'=> empty($row[0])? 0: $row[0], 
						'question'=> empty($row[1])? "": $row[1], 
						'answear'=> empty($row[2])? "": $row[2],
						'yes'=> empty($row[3])? 0: $row[3],
						'no'=> empty($row[4])? 0: $row[4]
			);
			$json = json_encode($json); 
		}	
		$mysqli->close();	
		return $json;
	}	

	
	/*function newOne($json)
	{
		$mysqli = new mysqli(HOST, USER, PASSWORD, DB);
		if($mysqli->connect_error)
			die("Error while connect database! ". $mysqli->connect_error);
		
		$arr = json_decode($json, true);
		
		$mysqli->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);
		$sql = sprintf("INSERT INTO nodes(QUESTION, ANSWEAR) VALUES(\"%s\", \"%s\")", 
		$arr['question'], $arr['answear']);
		
		if(!$mysqli->query($sql)) 
			die("Error while insert (".$mysqli->errno.") ".$mysqli->error);
		
		$sql = "SELECT LAST_INSERT_ID()";
		$res = $mysqli->query($sql);
		$row = $res->fetch_row();
		$lastId = $row[0];
					
		if((int) $arr['yes'] == 0)
			$sql = sprintf("UPDATE nodes n SET n.NO_NODE_ID = %d WHERE n.NODE_ID = %d", $lastId, $arr['current']); 
		else 
			$sql = sprintf("UPDATE nodes n SET n.YES_NODE_ID = %d WHERE n.NODE_ID = %d", $lastId, $arr['current']); 
		
		if(!$mysqli->query($sql)) 
			die("Error while update (".$mysqli->errno.") ".$mysqli->error);
		
		$mysqli->commit();
		$mysqli->close();
		return "OK";
	}*/	
	
 ?>

