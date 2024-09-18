#include <GXAnalyze.h>
#include <iostream>


class GXAnalyzeDev : public GXAnalyze {
public:
    long getSourceValue(string valuePath, void *source) override {
        return 0;
    }

    long getFunctionValue(string funName, long *paramPointers, int paramsSize, string source) override {
        return 0;
    }

    void throwError(string message) override {
    }
};

int main() {
    std::cout << "Hello, World!" << std::endl;
    GXAnalyze *analyze = new GXAnalyzeDev();

    return 0;
}
