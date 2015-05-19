<?php
require 'Slim/Slim.php';
include 'db.php';
\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();
$app->get('/users','getUsers');
$app->post('/users','postUsers');
$app->get('/users/:access_code','accessCodeData');
$app->get('/updates','getUserUpdates');
$app->get('/challenge/:access_code','getChallenges');
$app->post('/challenge/:access_code','postChallenges');
$app->get('/dummy', 'dummyTable');
$app->get('/hello', function () {
$app = \Slim\Slim::getInstance();
$app->response->setStatus(200);


    echo "Hello, ";
});
$app->run();


function getUsers() {

//$sql = "INSERT INTO `users` (`user_id`, `class`,`wins`) VALUES (7, `bond`,50);";
$sql = "SELECT user_id,access_code FROM `users`;";

try {
$app = \Slim\Slim::getInstance();
$db = getDB();
$stmt = $db->prepare($sql);
$stmt->execute();
$users = $stmt->fetchAll(PDO::FETCH_OBJ);
$db = null;
$app->response->setStatus(200);
echo "test table " . json_encode($users);
} catch(PDOException $e) {
echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}

function postUsers(){

   // $value=rand(0,9999);
   // echo $value;


// echo $sql
//$sql = "INSERT INTO `users` (`user_id`, `class`,`wins`) VALUES (7, `bond`,50, );";
//$sql = "SELECT user_id FROM `users`;";

    try {
        $app = \Slim\Slim::getInstance();
        $db = getDB();


            do{
                $value=rand(0,9999);
               // echo $value;
                $checkvalue = "SELECT user_id FROM users WHERE access_code = $value";

                $stmt = $db->prepare($checkvalue);
                $stmt->execute();
            }while($stmt->rowCount()>0);


            $sql = "INSERT INTO `users` (`user_id`, `access_code`, `class`,`wins`) VALUES (DEFAULT,$value,NULL,0);";
            $stmt = $db->prepare($sql);
            $stmt->execute();
            $users = $stmt->fetchAll(PDO::FETCH_OBJ);
            $db = null;
            $app->response->setStatus(200);
            echo $users;

        } catch(PDOException $e) {
            //echo ' ';
        }
        echo $value;
    }

function accessCodeData($access_code) {

$sql = "SELECT * FROM `users` WHERE access_code=:access_code;";
//$sql = "INSERT INTO `users` (`user_id`, `class`,`wins`) VALUES (7, `bond`,50);";
//$sql = "SELECT user_id FROM `users`;";

try {
$app = \Slim\Slim::getInstance();
$db = getDB();
$stmt = $db->prepare($sql);
$stmt->bindParam("access_code", $access_code, PDO::PARAM_INT);
$stmt->execute();
$users = $stmt->fetchAll(PDO::FETCH_OBJ);
//print_r($users);
$db = null;
$app->response->setStatus(200);
echo "test table " . json_encode($users);
} catch(PDOException $e) {
echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}

function getChallenges($access_code) {

try{
$app = \Slim\Slim::getInstance();
$db = getDB();
$find_id = "SELECT `user_id` FROM `users` WHERE access_code=:access_code;";
$stmt = $db->prepare($find_id);
$result = $stmt->execute(array(
   ':access_code' => $access_code
));

$users = $stmt->fetchColumn(0);
$user_encode = json_encode($users, true);
$returned_id=implode('', (array)$user_encode);

$sql = "SELECT * FROM `challenges` WHERE user_id=$returned_id;";
            $stmt = $db->prepare($sql);
            $stmt->execute();
            $result= $stmt->fetchAll(PDO::FETCH_OBJ);
            echo json_encode($result);
$db = null;
$app->response->setStatus(200);

} catch(PDOException $e) {
echo  '{"error":{"text":'. $e->getMessage() .'}}';
}
}


function postChallenges($access_code) {

try {
$app = \Slim\Slim::getInstance();
$db = getDB();

$find_id = "SELECT `user_id` FROM `users` WHERE access_code=:access_code;";
$stmt = $db->prepare($find_id);
$result = $stmt->execute(array(
   ':access_code' => $access_code
));

$users = $stmt->fetchColumn(0);
var_dump($users);
$user_encode = json_encode($users, true);
echo $user_encode;
$returned_id=implode('', (array)$user_encode);
echo $returned_id;
//user_decode =json_decode(($user_encode));


$sql = "INSERT INTO `challenges` (`challenge_id`,`user_id`, `from`) VALUES (DEFAULT,$returned_id,$access_code);";
            $stmt = $db->prepare($sql);
            $stmt->execute();
            $insertValues = $stmt->fetchAll(PDO::FETCH_OBJ);
 echo "test table " . json_encode($insertValues);

$db = null;
$app->response->setStatus(200);

} catch(PDOException $e) {
echo  '{"error":{"text":'. $e->getMessage() .'}}';
}
}

function getUserUpdates() {
$sql = "CREATE TABLE `updates` (
`update_id` int(11) AUTO_INCREMENT, `user_id` int(11),`location` char(10), `time` char(10),
PRIMARY KEY (`update_id`), FOREIGN KEY UFK(`user_id`) references `users`
);";


try {
$app = \Slim\Slim::getInstance();
$db = getDB();
$stmt = $db->prepare($sql);
$stmt->execute();
$db = null;
$app->response->setStatus(200);
echo 'Completed';
} catch(PDOException $e) {
echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}

function dummyTable() {

$sql = "SELECT user_id FROM users WHERE access_code = 200;";


try {
$app = \Slim\Slim::getInstance();
$db = getDB();
$stmt = $db->prepare($sql);
$stmt->execute();
$db = null;
$app->response->setStatus(200);
echo 'Completed';
} catch(PDOException $e) {
echo  '{"error":{"text":'. $e->getMessage() .'}}';
}
}
