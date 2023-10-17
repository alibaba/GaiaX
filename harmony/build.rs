fn main() {
    cxx_build::bridge("src/lib.rs")
        .flag_if_supported("-std=c++14")
        .compile("cxxbridge-gaiax");

    println!("cargo:rerun-if-changed=src/main.rs");
    println!("cargo:rerun-if-changed=src/blobstore.cc");
    println!("cargo:rerun-if-changed=include/blobstore.h");
}
