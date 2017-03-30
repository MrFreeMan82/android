<?php	
	if(array_key_exists("test", $_GET))
	{
		//print "Test OK.";
		// After programm reinstall this key will be changed
		$key = "dw-sM0qc8Uc:APA91bHavMyXSHGCpWZobdAIwoORcrD5S4P39f-CTCkSNJoeo7ZhlqaJk5tTGBlDyrzW5fCFL_bJIOmuNjQv0F-Dxr_Gfik9uuh7n6qVMIY8MXBBJzK6KDF2bCLwjwYr_-B026OrGazz";
		push("Test OK", $key);
		exit;
	}

	function push($msg, $key)
	{
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array (
			'notification' => array(
				'title' => "Title Test",
				'text' => $msg
			),
			'to'=> $key
		);
		$fields = json_encode ($fields);
		$headers = array (
            'Authorization: key='."AAAAeR61_bw:APA91bEorBzO1tCQLQmdUSlF7cPX1X2klUkGmFl5fbWrqEtyjSMX-dKPDLgmR5b-4D8q_2iYW_7ip-7VX4hMxPQ4qlsw7tYp9NHFTE1HSg2FLqgcCfgfX-laNZopHpsh5fDjy_K20b9A",
            'Content-Type: application/json'
		);
		$ch = curl_init();
		curl_setopt ($ch, CURLOPT_URL, $url);
		curl_setopt ($ch, CURLOPT_POST, true);
		curl_setopt ($ch, CURLOPT_HTTPHEADER, $headers);
		curl_setopt ($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt ($ch, CURLOPT_POSTFIELDS, $fields);

		$result = curl_exec ($ch);
		print $result;
		curl_close ($ch);
	}
	
 ?>

