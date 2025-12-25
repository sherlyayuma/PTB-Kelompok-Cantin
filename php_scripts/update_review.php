<?php
include 'connection.php';

$id = $_POST['id'];
$rating = $_POST['rating'];
$review_text = $_POST['review_text'];

// DEBUG LOGGING
file_put_contents("debug_update.txt", "ID: $id, Rating: $rating, Text: $review_text\n", FILE_APPEND);

// Update query (Base)
$sql = "UPDATE reviews SET rating = '$rating', review_text = '$review_text'";

// Handle Image Upload if present
if(isset($_FILES['image']['name'])){
    $imageName = time() . "_" . $_FILES['image']['name'];
    $target_dir = "uploads/reviews/";
    if(!is_dir($target_dir)){ mkdir($target_dir, 0777, true); }
    
    move_uploaded_file($_FILES['image']['tmp_name'], $target_dir . $imageName);
    $sql .= ", image = '$target_dir$imageName'";
}

// Handle Video Upload if present
if(isset($_FILES['video']['name'])){
    $videoName = time() . "_" . $_FILES['video']['name'];
    $target_dir = "uploads/reviews/";
    if(!is_dir($target_dir)){ mkdir($target_dir, 0777, true); }
    
    move_uploaded_file($_FILES['video']['tmp_name'], $target_dir . $videoName);
    $sql .= ", video = '$target_dir$videoName'";
}

$sql .= " WHERE id = '$id'";

if(mysqli_query($connection, $sql)){
    echo json_encode(array("status" => "success", "message" => "Review updated successfully"));
} else {
    file_put_contents("debug_update.txt", "Error: " . mysqli_error($connection) . "\nSQL: $sql\n", FILE_APPEND);
    echo json_encode(array("status" => "error", "message" => "Failed to update review"));
}
?>
