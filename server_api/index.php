<?php
require 'Slim/Slim.php';
include 'db.php';
\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();
$app->get('/users','getUsers');
$app->post('/users','postUsers');
$app->get('/updates','getUserUpdates');
$app->get('/challenges','getChallenges');
$app->get('/dummy', 'dummyTable');
$app->get('/hello', function () {
$app = \Slim\Slim::getInstance();
$app->response->setStatus(200);


    echo "Hello, ";
});
$app->run();


function getUsers() {
$sql = "CREATE TABLE `users` (
`user_id` int(11) AUTO_INCREMENT, `wins` int(100), `class` char(50),
PRIMARY KEY (`user_id`)
);";
$sql = "INSERT INTO `users` (`user_id`, `class`,`wins`) VALUES (7, `bond`,50);";
//$sql = "SELECT user_id FROM `users`;";

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
function postUsers() {

$value=rand(0,9999);
echo $value;


// echo $sql
//$sql = "INSERT INTO `users` (`user_id`, `class`,`wins`) VALUES (7, `bond`,50, );";
//$sql = "SELECT user_id FROM `users`;";

try {
$app = \Slim\Slim::getInstance();
$db = getDB();


    do{
        $value=rand(0,9999);
        $checkvalue = "SELECT user_id FROM users WHERE access_code = $value";

        $stmt = $db->prepare($checkvalue);
        $stmt->execute();
      }while($stmt->rowCount()>0);


$sql = "INSERT INTO `users` (`user_id`, `access_code`, `class`,`wins`) VALUES (DEFAULT, $value,NULL,0);";
 $stmt = $db->prepare($sql);
 $stmt->execute();
$users = $stmt->fetchAll(PDO::FETCH_OBJ);
$db = null;
$app->response->setStatus(200);
echo $value;
} catch(PDOException $e) {
//echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}
function getChallenges() {
$sql = "CREATE TABLE `challenges` (
`challenge_id` int(11) AUTO_INCREMENT, `user_id` int(11),`user1` char(10), `user2` char(10), `button_chosen` char(100), `time` char(50),
PRIMARY KEY (`challenge_id`), FOREIGN KEY UFK(`user_id`) references `users`
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
