<template lang="">
  <div class="container-fluid">
    <div class="row justify-content-start">
      <div
        class="row justify-content-start"
        style="font-size: 10px; margin-top: 1rem; margin-bottom: 1rem"
      >
        * 최대 4장까지 사진 등록이 가능합니다.
      </div>

      <div class="row justify-content-start">
        <div v-if="!files.length" class="row justify-content-start">
          <div class="row justify-content-start">
            <div class="col">
              <div
                class="image-box"
                style="
                  width: 100px;
                  height: 40px;
                  flex-shrink: 0;
                  border-radius: 10px;
                  background: #2191ff;
                  color: #ffffff;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                "
              >
                <label for="file">사진 등록</label>
                <input
                  type="file"
                  id="file"
                  ref="files"
                  @change="imageUpload"
                />
              </div>
            </div>
          </div>
        </div>
        <div v-else class="file-preview-content-container">
          <div class="file-preview-container">
            <div
              v-for="(file, index) in files"
              :key="index"
              class="file-preview-wrapper"
            >
              <div
                class="file-close-button"
                @click="fileDeleteButton"
                :name="file.number"
              >
                x
              </div>
              <img :src="file.preview" />
            </div>
            <div v-if="files.length < 4">
              <div
                class="image-box"
                style="
                  width: 120px;
                  height: 40px;
                  flex-shrink: 0;
                  border-radius: 10px;
                  background: #cecece;
                  color: #ffffff;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                "
              >
                <label for="file">추가 사진 등록</label>
                <input
                  type="file"
                  id="file"
                  ref="files"
                  @change="imageUpload"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="cropImgURL" class="row justify-content-start">
      <div class="col">
        <cropper
          class="cropper"
          ref="cropper"
          :auto-zoom="true"
          :stencil-size="{
            width: 280,
            height: 280,
          }"
          image-restriction="stencil"
          :stencil-props="{
            aspectRatio: 1 / 1,
          }"
          :src="cropImgURL"
        />
      </div>
      <div
        style="
          width: 80px;
          height: 40px;
          flex-shrink: 0;
          border-radius: 10px;
          background: #2191ff;
          color: #ffffff;
          display: flex;
          justify-content: center;
          align-items: center;
        "
        @click="uploadImage"
      >
        저장
      </div>
    </div>
  </div>
</template>
<script>
import { Cropper } from "vue-advanced-cropper";
import "vue-advanced-cropper/dist/style.css";

export default {
  data() {
    return {
      files: [], //업로드용 파일
      filesPreview: [],
      uploadImageIndex: 0, // 이미지 업로드를 위한 변수
      img: "https://images.pexels.com/photos/4323307/pexels-photo-4323307.jpeg",
      cropImgURL: "",
      currImgList: [],
      currFileList: [],
    };
  },

  components: {
    Cropper,
  },
  methods: {
    makePreview(blobData) {
      for (let i = 0; i < this.$refs.files.files.length; i++) {
        this.files = [
          ...this.files,
          //이미지 업로드
          {
            //실제 파일
            file: this.cropImgURL,
            //이미지 프리뷰
            preview: this.cropImgURL,
            //삭제및 관리를 위한 number
            number: this.uploadImageIndex,
          },
        ];
        //이미지 업로드용 프리뷰
        this.filesPreview = [
          ...this.filesPreview,
          {
            file: URL.createObjectURL(this.$refs.files.files[i]),
            number: this.uploadImageIndex,
            binaryFile: blobData,
          },
        ];
      }
      this.uploadImageIndex++; //이미지 index의 마지막 값 + 1 저장

      this.cropImgURL = "";
      this.currImgList = this.filesPreview.map((row) => row.file);
      this.currFileList = this.filesPreview.map((row) => row.binaryFile);
    },
    imageUpload() {
      this.cropImgURL = URL.createObjectURL(
        this.$refs.files.files[this.$refs.files.files.length - 1]
      );
    },

    imageAddUpload() {
      // this.files = [...this.files, this.$refs.files.files];
      //하나의 배열로 넣기c
      let num = -1;
      for (let i = 0; i < this.$refs.files.files.length; i++) {
        this.files = [
          ...this.files,
          //이미지 업로드
          {
            //실제 파일
            file: this.$refs.files.files[i],
            //이미지 프리뷰
            preview: URL.createObjectURL(this.$refs.files.files[i]),
            //삭제및 관리를 위한 number
            number: i + this.uploadImageIndex,
          },
        ];
        num = i;
      }
      this.uploadImageIndex = this.uploadImageIndex + num + 1;
    },
    fileDeleteButton(e) {
      const name = e.target.getAttribute("name");
      this.files = this.files.filter((data) => data.number !== Number(name));
      this.filesPreview = this.filesPreview.filter(
        (data) => data.number !== Number(name)
      );
      this.currImgList = this.filesPreview.map((row) => row.file);
      this.currFileList = this.filesPreview.map((row) => row.binaryFile);
    },
    uploadImage() {
      const { canvas } = this.$refs.cropper.getResult();
      if (canvas) {
        var blobData = "";
        canvas.toBlob((blob) => {
          this.cropImgURL = canvas.toDataURL();

          this.makePreview(blob);
          blobData = blob;
        });

        console.log(blobData);
      }
    },
  },
  change({ coordinates, canvas }) {
    console.log(coordinates, canvas);
  },
  watch: {
    cropImgURL(newVal) {
      this.cropImgURL = newVal;
    },
    currFileList(newVal) {
      this.currFileList = newVal;
      this.$emit("updateImg", this.currFileList);
    },
  },
};
</script>
<style scoped>
.room-deal-information-container {
  margin-top: 50px;
  color: #222222;
  border: 1px solid #dddddd;
}

.room-deal-information-title {
  text-align: center;
  font-size: 18px;
  line-height: 60px;
  border-bottom: 1px solid #dddddd;
}

.room-deal-information-content-wrapper {
  min-height: 50px;
  display: flex;
}

.room-deal-informtaion-content-title {
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 150px;
  background-color: #f9f9f9;
}

.room-deal-information-content {
  width: 100%;
  font-size: 14px;
}

.room-deal-option-selector {
  display: flex;
  align-items: center;
  padding: 15px;
}

.room-deal-option-item {
  width: 100px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #cccccc;
  border-radius: 5px;
  cursor: pointer;
}

.room-deal-option-item:last-child {
  margin-left: 10px;
}

.room-deal-option-notice {
  margin-left: auto;
  font-size: 14px;
  color: #888888;
}

.room-deal-option-item-deposit {
  margin-left: 10px;
}

.room-deal-information-wrapper {
  display: flex;
  flex-direction: column;
}

.room-deal-information-option {
  padding: 10px;
  display: flex;
  align-items: center;
}

.room-deal-information-option:last-child {
  border-bottom: 1px solid #dddddd;
}

.room-deal-information-item-type {
  font-size: 13px;
  color: #fff;
  background-color: #61b6e5;
  min-width: 50px;
  height: 26px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 3px;
}

.room-deal-information-item-wrapper {
  display: flex;
  align-items: center;
  margin-left: 10px;
  height: 46px;
  width: 100%;
}

.room-deal-information-item-wrapper > input {
  border: 1px solid #dddddd;
  width: 140px;
  height: 100%;
  padding: 0 15px;
  font-size: 15px;
}

.room-deal-inforamtion-won {
  margin: 0 10px;
}

.room-deal-information-example {
  color: #888888;
}

.room-deal-information-option:not(:first-child) {
  margin-top: 10px;
}

.room-deal-inforamtion-divide {
  font-size: 22px;
  margin: 0 8px;
  color: #222222;
  font-weight: 100;
}

.room-deal-close-button-wrapper {
  margin-left: auto;
  cursor: pointer;
}

.room-deal-close-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  background-color: #666666;
  color: rgb(220, 220, 220);
}

.room-deal-cliked {
  background-color: rgb(235, 235, 235);
  color: rgb(170, 170, 170);
}

.room-file-upload-example {
  height: 100%;
}

.room-write-content-container {
  border-top: 1px solid #dddddd;
  min-height: 260px;
}

.room-picture-notice {
  margin: 20px;
  padding: 20px 40px;
  border: 1px solid #dddddd;
}

.file-preview-content-container {
  height: 100%;
}

.room-file-upload-example-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.room-file-image-example-wrapper {
  text-align: center;
}

.image-box input[type="file"] {
  position: absolute;
  width: 0;
  height: 0;
  padding: 0;
  overflow: hidden;
  border: 0;
}

.file-preview-wrapper {
  padding: 10px;
  position: relative;
}

.file-preview-wrapper > img {
  position: relative;
  width: 150px;
  height: 150px;
  z-index: 10;
}

.file-close-button {
  position: absolute;
  /* align-items: center; */
  line-height: 18px;
  z-index: 98;
  font-size: 18px;
  right: 5px;
  top: 10px;
  color: black;
  font-weight: bold;

  width: 20px;
  height: 20px;
  text-align: center;
  cursor: pointer;
}

.file-preview-container {
  height: 100%;
  display: flex;
  flex-wrap: wrap;
}

.file-preview-wrapper-upload {
  margin: 10px;
  padding-top: 20px;
  background-color: #888888;
  width: 190px;
  height: 130px;
}

.room-write-button-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #222222;
}

.room-write-button-wrapper > div {
  width: 160px;
  height: 50px;
  border: 1px solid #dddddd;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 15px;
  cursor: pointer;
}

.room-write-button {
  margin-left: 15px;
  color: #fff;
  background-color: #1564f9;
}

.room-write-button:hover {
  opacity: 0.8;
}

.cropper {
  max-height: 250px;
  max-width: 250px;
  background: #ddd;
}
</style>
