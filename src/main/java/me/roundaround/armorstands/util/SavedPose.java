package me.roundaround.armorstands.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.roundaround.armorstands.util.Pose.PoseSupplier;
import net.minecraft.util.math.EulerAngle;

public class SavedPose implements PoseSupplier {
  private final String name;
  private final EulerAngle head;
  private final EulerAngle body;
  private final EulerAngle rightArm;
  private final EulerAngle leftArm;
  private final EulerAngle rightLeg;
  private final EulerAngle leftLeg;

  public SavedPose(
      String name,
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg
  ) {
    this.name = name;
    this.head = head;
    this.body = body;
    this.rightArm = rightArm;
    this.leftArm = leftArm;
    this.rightLeg = rightLeg;
    this.leftLeg = leftLeg;
  }

  public SavedPose(String name, Pose pose) {
    this(
        name,
        pose.getHead(),
        pose.getBody(),
        pose.getRightArm(),
        pose.getLeftArm(),
        pose.getRightLeg(),
        pose.getLeftLeg()
    );
  }

  @Override
  public Pose toPose() {
    return new Pose(head, body, rightArm, leftArm, rightLeg, leftLeg);
  }

  public String getName() {
    return name;
  }

  public EulerAngle getHead() {
    return head;
  }

  public EulerAngle getBody() {
    return body;
  }

  public EulerAngle getRightArm() {
    return rightArm;
  }

  public EulerAngle getLeftArm() {
    return leftArm;
  }

  public EulerAngle getRightLeg() {
    return rightLeg;
  }

  public EulerAngle getLeftLeg() {
    return leftLeg;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("name", name);

    json.add("head", eulerAngleToJson(head));
    json.add("body", eulerAngleToJson(body));
    json.add("rightArm", eulerAngleToJson(rightArm));
    json.add("leftArm", eulerAngleToJson(leftArm));
    json.add("rightLeg", eulerAngleToJson(rightLeg));
    json.add("leftLeg", eulerAngleToJson(leftLeg));

    return json;
  }

  public static SavedPose fromJson(JsonObject json) {
    String name = json.get("name").getAsString();
    EulerAngle head = jsonToEulerAngle(json.getAsJsonArray("head"));
    EulerAngle body = jsonToEulerAngle(json.getAsJsonArray("body"));
    EulerAngle rightArm = jsonToEulerAngle(json.getAsJsonArray("rightArm"));
    EulerAngle leftArm = jsonToEulerAngle(json.getAsJsonArray("leftArm"));
    EulerAngle rightLeg = jsonToEulerAngle(json.getAsJsonArray("rightLeg"));
    EulerAngle leftLeg = jsonToEulerAngle(json.getAsJsonArray("leftLeg"));

    return new SavedPose(name, head, body, rightArm, leftArm, rightLeg, leftLeg);
  }

  private static EulerAngle jsonToEulerAngle(JsonArray json) {
    float pitch = json.get(0).getAsFloat();
    float yaw = json.get(1).getAsFloat();
    float roll = json.get(2).getAsFloat();

    return new EulerAngle(pitch, yaw, roll);
  }

  private static JsonArray eulerAngleToJson(EulerAngle angle) {
    JsonArray json = new JsonArray();
    json.add(angle.getPitch());
    json.add(angle.getYaw());
    json.add(angle.getRoll());

    return json;
  }
}
