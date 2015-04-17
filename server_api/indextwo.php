<?php

require 'Slim/Slim.php';

\Slim\Slim::registerAutoloader();


$app = new \Slim\Slim();

$app->get('/users','getUsers');
$app->get('/updates','getUserUpdates');
$app->get('/challenges,'getChallenges');
$app->put('/users/:query','putUsers');
$app->post('/updates','putUpdates');
$app->post('/challenges','putChallenges');
$app->get('/users/:query','getUser');

$app->run();

function getUsers(){
$sql = "SELECT user_id,wins,class FROM users ORDER BY user_id DESC";

try {
$db = getDB();
$stmt = $db->query($sql);
$users = $stmt->fetchAll(PDO::FETCH_OBJ);
$db = null;
echo '{"users": ' . json_encode($users) . '}';
} catch(PDOException $e) {
//error_log($e->getMessage(), 3, '/var/tmp/phperror.log'); //Write error log
echo '{"error":{"text":'. $e->getMessage() .'}}';
}

}

function getUserUpdates(){

}

function getChallenges(){

}

function putUsers(){

$body = $app->request()->getBody();
$file= 'testfile.txt';

$current = file_get_contents($file);
$current .= $body . "\n";
file_put_contents($file, $current);

function putUpdates(){

}
function putChallenges(){

}
function getUser(){

}


?>
