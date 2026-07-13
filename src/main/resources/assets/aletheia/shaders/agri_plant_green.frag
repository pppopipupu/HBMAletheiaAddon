#version 120

uniform sampler2D texture;
uniform float iTime;

void main() {
    vec2 uv = gl_TexCoord[0].st;
    vec4 texColor = texture2D(texture, uv);

    if (texColor.a < 0.05) {
        discard;
    }

    float pulse = 0.9 + 0.1 * sin(iTime * 3.0);

    vec3 base = texColor.rgb * vec3(0.25, 1.1, 0.45);
    vec3 tint = base + vec3(0.0, 0.12, 0.04) * pulse;

    gl_FragColor = vec4(tint * pulse, texColor.a);
}
