<?php
$masterLoginInput = $_POST['masterLoginInput'];
$masterPassInput = $_POST['masterPassInput'];
$target1LoginInput = $_POST['target1LoginInput'];
$target1PassInput = $_POST['target1PassInput'];
$target2LoginInput = $_POST['target2LoginInput'];
$target2PassInput = $_POST['target2PassInput'];
$masterLatitude = $_POST['masterLatitude'];
$masterLongitude = $_POST['masterLongitude'];
$masterBatteryLevel = $_POST['masterBatteryLevel'];
$masterUserMessage = $_POST['masterUserMessage'];
$masterServiceMessage = $_POST['masterServiceMessage'];

$link = mysql_connect("localhost", "smartns4_gpsadm", "tzM;(HBe4QSW");
mysql_select_db("smartns4_gps_devices", $link);

// ���������, ��������� �� ����� ��������� �� �� ����� ���� masterLoginInput/masterPassInput
$result = mysql_query("SELECT lastlocationtime FROM gps_devices WHERE name='" . $masterLoginInput . "' AND pass='" . $masterPassInput . "'");
$row = mysql_fetch_array($result);
if ($row['lastlocationtime'] != "") { // ��������� �� masterLoginInput/masterPassInput ��� ���������
    $whereapply = "UPDATE gps_devices SET latitude='" . $masterLatitude . "', longitude='" . $masterLongitude . "', batterylevel='" . $masterBatteryLevel . "', usermessage='" . $masterUserMessage . "', servicemessage='" . $masterServiceMessage . "', lastlocationtime='" . date("Y-m-d H:i:s") . "' WHERE name='" . $masterLoginInput . "' AND pass='" . $masterPassInput . "'";
    $result = mysql_query($whereapply);
} else { // ��������� �� masterLoginInput/masterPassInput ��� �� ���� �� ���������, ������� ����� ������ � ����
    $whereapply = "INSERT INTO gps_devices (`id`, `name`, `pass`, `latitude`, `longitude`, `batterylevel`, `usermessage`, `servicemessage`, `lastlocationtime`) VALUES (NULL, '" . $masterLoginInput . "', '" . $masterPassInput . "', '" . $masterLatitude . "', '" . $masterLongitude . "', '" . $masterBatteryLevel . "', '" . $masterUserMessage . "', '" . $masterServiceMessage . "', '" . date("Y-m-d H:i:s") . " (server time)');";
    $result = mysql_query($whereapply);
}
?>
{ "target1Latitude": "<?php
$result = mysql_query("SELECT * FROM gps_devices WHERE name='" . $target1LoginInput . "' AND pass='" . $target1PassInput . "'");
$row = mysql_fetch_array($result);
echo $row['latitude']; ?>", 
"target1Longitude": "<?php echo $row['longitude']; ?>",
"target1LastLocationTime": "<?php echo $row['lastlocationtime']; ?>",
"target1BatteryLevel": "<?php echo $row['batterylevel']; ?>",
"target1UserMessage": "<?php
if (((int) $row['servicemessage'] % 10 == 1) || ((int) $row['servicemessage'] % 10 == 3))
    echo $row['usermessage'];
?>",
"target1ServiceMessage": "<?php echo $row['servicemessage']; ?>",
"target2Latitude": "<?php
$result = mysql_query("SELECT * FROM gps_devices WHERE name='" . $target2LoginInput . "' AND pass='" . $target2PassInput . "'");
$row = mysql_fetch_array($result);
echo $row['latitude']; ?>",
"target2Longitude": "<?php echo $row['longitude']; ?>",
"target2LastLocationTime": "<?php echo $row['lastlocationtime']; ?>",
"target2BatteryLevel": "<?php echo $row['batterylevel']; ?>",
"target2UserMessage": "<?php
if (((int) $row['servicemessage'] % 10 == 2) || ((int) $row['servicemessage'] % 10 == 3))
    echo $row['usermessage'];
?>",
"target2ServiceMessage": "<?php echo $row['servicemessage']; ?>"
}
