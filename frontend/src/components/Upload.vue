<template>
  <div>
    <h1>Hello</h1>
    <input type="file" @change="onFileSelected" accept="image/png, image/jpg, image/jpeg"/>
    <button>Upload Button</button>
  </div>
</template>

<script>

const Upload = {
  name: "Upload",
  data () {
    return {
      selectedFile: null
    }
  },
  methods: {
  }
}
export default Upload;
</script>

<style scoped>

</style>