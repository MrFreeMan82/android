<?php
	if(array_key_exists("next", $_GET)) 
	{
		print getNext();
		exit;
	}
	else if(array_key_exists("new", $_POST))
	{
		//print_r("hghfgghgfhgfhg");
		$json = $_POST["new"];
		
		print newOne($json);
		//print "OK";
		exit;
	}
	else die("Key does not exists");
	
	function newOne($json)
	{
		$mysqli = new mysqli("127.0.0.1", "root", "", "animals");
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
		if(empty($id)) die("Empty GET next value");

		$mysqli = new mysqli("127.0.0.1", "root", "", "animals");
		if($mysqli->connect_error)
			die("Error while connect database! ". $mysqli->connect_error);

		$sql = sprintf("SELECT NODE_ID, QUESTION, ANSWEAR, YES_NODE_ID, NO_NODE_ID FROM nodes WHERE NODE_ID = %d", $id);
		if($mysqli->multi_query($sql))
		{
			if($result = $mysqli->store_result())
			{
				if($result->num_rows === 0)
				{
					$json = "Empty result with id = ".$id;
				} else {
					$row = $result->fetch_row();
					$json = array(
					'node_id'=> empty($row[0])? 0: $row[0], 
					'question'=> empty($row[1])? "": $row[1], 
					'answear'=> empty($row[2])? "": $row[2],
					'yes'=> empty($row[3])? 0: $row[3],
					'no'=> empty($row[4])? 0: $row[4]);
					$json = json_encode($json); 
				}
				$result->free();
			}
		}
		$mysqli->close();	
		return $json;
	}		
	
 ?>

